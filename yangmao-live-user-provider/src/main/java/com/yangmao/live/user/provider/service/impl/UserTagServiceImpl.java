package com.yangmao.live.user.provider.service.impl;

import static com.yangmao.live.user.constants.UserTagFieldNameConstants.TAG_INFO_01;
import static com.yangmao.live.user.constants.UserTagFieldNameConstants.TAG_INFO_02;
import static com.yangmao.live.user.constants.UserTagFieldNameConstants.TAG_INFO_03;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangmao.live.common.ConvertBeanUtils;
import com.yangmao.live.framework.redis.starter.key.builder.UserProviderCacheKeyBuilder;
import com.yangmao.live.user.constants.CacheAsyncDeleteCodeEnum;
import com.yangmao.live.user.constants.RocketMqConstants;
import com.yangmao.live.user.constants.UserTagsEnum;
import com.yangmao.live.user.dto.UserCacheAsyncDeleteDTO;
import com.yangmao.live.user.dto.UserTagDTO;
import com.yangmao.live.user.provider.dao.mapper.IUserTagMapper;
import com.yangmao.live.user.provider.dao.po.UserTagPO;
import com.yangmao.live.user.provider.service.IUserTagService;
import com.yangmao.live.user.utils.TagInfoUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-16
 */
@Service
@Slf4j
public class UserTagServiceImpl extends ServiceImpl<IUserTagMapper, UserTagPO> implements IUserTagService {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private MQProducer mqProducer;

    private static final String OK = "OK";

    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        boolean updateStatus = this.baseMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if(updateStatus){
//todo            this.deleteByUserIdFromRedis(userId);
            return true;
        }
        //如果存在该标签（即重复插入），直接返回false
        UserTagPO userTagPO = this.getById(userId);
        if(userTagPO!=null){
            return false;
        }
        //如果是因为不存在该用户的记录失败的，应该先插入一行用户记录，再增加标签
        //高并发的情况下可能会重复插入 -> redis分布式锁
        String key = cacheKeyBuilder.buildTagLockKey(userId);
        String setNxResult = (String) redisTemplate.execute((RedisCallback<String>) connection -> {
            RedisSerializer keySerializer = redisTemplate.getKeySerializer();
            RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
            return (String) connection.execute("set",
                    keySerializer.serialize(key),
                    valueSerializer.serialize("-1"),
                    "NX".getBytes(StandardCharsets.UTF_8),
                    "EX".getBytes(StandardCharsets.UTF_8),
                    "3".getBytes(StandardCharsets.UTF_8));
        });
        if(!OK.equals(setNxResult)){
            return false;
        }
        userTagPO = UserTagPO.builder()
                .userId(userId)
                .build();
        this.baseMapper.insert(userTagPO);
        redisTemplate.delete(key);
        return this.baseMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        boolean cancelStatus = this.baseMapper.cancelTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if(!cancelStatus){
            return false;
        }
//todo        this.deleteByUserIdFromRedis(userId);
        return true;
    }

    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        UserTagDTO userTagDTO = this.queryByUserId(userId);
        String fieldName = userTagsEnum.getFieldName();
        Long tagInfo = this.getTagInfo(userTagDTO, fieldName);
        return TagInfoUtils.isContain(tagInfo, userTagsEnum.getTag());
    }

    private Long getTagInfo(UserTagDTO userTagDTO, String fieldName) {
        return switch (fieldName) {
            case TAG_INFO_01 -> userTagDTO.getTagInfo01();
            case TAG_INFO_02 -> userTagDTO.getTagInfo02();
            case TAG_INFO_03 -> userTagDTO.getTagInfo03();
            default -> 0L;
        };
    }


    private void deleteByUserIdFromRedis(Long userId){
        String key = cacheKeyBuilder.buildTagInfoKey(userId);
        redisTemplate.delete(key);

        HashMap<String, Object> json = new HashMap<>();
        json.put("userId",userId);

        UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = UserCacheAsyncDeleteDTO.builder()
                .code(CacheAsyncDeleteCodeEnum.USER_TAG_DELETE.getCode())
                .json(JSON.toJSONString(json))
                .build();

        try {
            Message message = new Message();
            message.setTopic(RocketMqConstants.USER_CACHE_ASYNC_DELETE_TOPIC);
            message.setBody(JSON.toJSONString(userCacheAsyncDeleteDTO).getBytes());
            //延迟级别，表示延迟一秒发送
            message.setDelayTimeLevel(1);
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询用户标签信息
     * @param userId
     * @return
     */
    private UserTagDTO queryByUserId(Long userId){
        String key = cacheKeyBuilder.buildUserInfoKey(userId);
        UserTagDTO userTagDTO = (UserTagDTO) redisTemplate.opsForValue().get(key);
        if(userTagDTO != null){
            return userTagDTO;
        }
        UserTagPO userTagPO = this.getById(userId);
        if(userTagPO == null){
            return null;
        }
        userTagDTO = ConvertBeanUtils.convert(userTagPO, UserTagDTO.class);
        redisTemplate.opsForValue().set(key,userTagDTO,30, TimeUnit.MINUTES);
        return userTagDTO;
    }


}
