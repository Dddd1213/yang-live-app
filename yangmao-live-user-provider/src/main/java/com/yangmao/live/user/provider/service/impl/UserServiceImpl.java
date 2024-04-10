package com.yangmao.live.user.provider.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangmao.live.common.ConvertBeanUtils;
import com.yangmao.live.framework.redis.starter.key.builder.UserProviderCacheKeyBuilder;
import com.yangmao.live.user.dto.UserDTO;
import com.yangmao.live.user.provider.dao.mapper.IUserMapper;
import com.yangmao.live.user.provider.dao.po.UserPO;
import com.yangmao.live.user.provider.service.IUserService;

import jakarta.annotation.Resource;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<IUserMapper, UserPO> implements IUserService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;
    @Resource
    private MQProducer mqProducer;


    @Override
    public UserDTO getByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId);
        UserDTO userDTO = (UserDTO) redisTemplate.opsForValue().get(key);
        if(userDTO != null){
            return userDTO;
        }
        userDTO = ConvertBeanUtils.convert(this.getById(userId), UserDTO.class);
        if(userDTO != null){
            redisTemplate.opsForValue().set(key,userDTO,createRadomExpireTime(), TimeUnit.SECONDS);
        }
        return userDTO;
    }

    @Override
    public Boolean updateUser(UserDTO userDTO) {
        if(userDTO == null || userDTO.getUserId()==null){
            return false;
        }
        this.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));

        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId());
        redisTemplate.delete(key);
        try {
            Message message = new Message();
            message.setTopic("user-update-cache");
            message.setBody(JSON.toJSONString(userDTO).getBytes());
            //延迟级别，表示延迟一秒发送
            message.setDelayTimeLevel(1);
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public Boolean insertOne(UserDTO userDTO) {
        if(userDTO == null|| userDTO.getUserId()==null){
            return false;
        }
        return this.save(ConvertBeanUtils.convert(userDTO, UserPO.class));
    }

    @Override
    public Map<Long, UserDTO> batchGetUserInfo(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)){
            return new HashMap<>();
        }
        //redis
        List<String> keyList = userIds.stream()
                .map(userId -> userProviderCacheKeyBuilder.buildUserInfoKey(userId))
                .toList();
        List<UserDTO> cacheUserDTOS = redisTemplate.opsForValue().multiGet(keyList);
        List<UserDTO> userDTOS = new ArrayList<>();
        List<Long> userIdsNotInCache = new ArrayList<>();
        if(!CollectionUtils.isEmpty(cacheUserDTOS)){
            userDTOS = cacheUserDTOS.stream().filter(Objects::nonNull).toList();
            List<Long> userIdsInCache = userDTOS.stream().map(UserDTO::getUserId).toList();
            userIdsNotInCache = userIds.stream().filter(userId -> !userIdsInCache.contains(userId)).toList();
        }

        if(!CollectionUtils.isEmpty(userDTOS) && userDTOS.size()==userIds.size()){
            return userDTOS.stream().collect(Collectors.toMap(UserDTO::getUserId, userDTO -> userDTO));
        }

        //对于不在缓存中的userId，再用数据库查
        Map<Long, List<Long>> userIdMap = userIdsNotInCache.stream()
                .collect(Collectors.groupingBy(userId -> userId % 10));

        //并行流加快速度
        //要是用线程安全的ArrayList
        List<UserDTO> dbUserDTOS = new CopyOnWriteArrayList<>();
         userIdMap.values().parallelStream()
                .forEach(userIdList ->
                        dbUserDTOS.addAll(ConvertBeanUtils.convertList(this.baseMapper.selectBatchIds(userIdList),UserDTO.class)));

         userDTOS = new ArrayList<>(userDTOS);
         userDTOS.addAll(dbUserDTOS);

         //放入缓存
        Map<String, UserDTO> dbUserMap = dbUserDTOS.stream().collect(Collectors.toMap(userDTO -> userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId()), userDTO -> userDTO));
        redisTemplate.opsForValue().multiSet(dbUserMap);
        //用管道批量传输命令，减小网络IO开销
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                for(String key:dbUserMap.keySet()){
                     operations.expire((K)key,createRadomExpireTime(),TimeUnit.SECONDS);
                }
                return null;
            }
        });

        return userDTOS.stream().collect(Collectors.toMap(UserDTO::getUserId, userDTO -> userDTO));
    }

    private int createRadomExpireTime(){
        int time = ThreadLocalRandom.current().nextInt(1000);
        return time + 60*30;
    }
}
