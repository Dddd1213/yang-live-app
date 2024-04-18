package com.yangmao.live.user.provider.config.rocketMQ;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;
import com.yangmao.live.framework.redis.starter.key.builder.UserProviderCacheKeyBuilder;
import com.yangmao.live.user.constants.CacheAsyncDeleteCodeEnum;
import com.yangmao.live.user.constants.RocketMqConstants;
import com.yangmao.live.user.dto.UserCacheAsyncDeleteDTO;

import jakarta.annotation.Resource;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-10
 */
@Configuration
public class RocketMQConsumerConfig implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQConsumerConfig.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + RocketMQConsumerConfig.class.getSimpleName());
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        mqPushConsumer.subscribe(RocketMqConstants.USER_CACHE_ASYNC_DELETE_TOPIC, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            String json = new String(msgs.get(0).getBody());
            UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = JSON.parseObject(json, UserCacheAsyncDeleteDTO.class);
            if (CacheAsyncDeleteCodeEnum.USER_INFO_DELETE.getCode() == userCacheAsyncDeleteDTO.getCode()) {
                Long userId = JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                redisTemplate.delete(cacheKeyBuilder.buildUserInfoKey(userId));
                LOGGER.info("延迟删除用户信息缓存，userId is {}",userId);
            } else if (CacheAsyncDeleteCodeEnum.USER_TAG_DELETE.getCode() == userCacheAsyncDeleteDTO.getCode()) {
                Long userId = JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                redisTemplate.delete(cacheKeyBuilder.buildTagInfoKey(userId));
                LOGGER.info("延迟删除用户标签缓存，userId is {}",userId);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        LOGGER.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }


}
