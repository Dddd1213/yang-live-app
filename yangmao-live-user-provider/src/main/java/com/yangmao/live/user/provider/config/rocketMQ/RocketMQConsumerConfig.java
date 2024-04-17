package com.yangmao.live.user.provider.config.rocketMQ;

import static com.yangmao.live.user.constants.RocketMqConstants.USER_CACHE_ASYNC_DELETE_TOPIC;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;
import com.yangmao.live.framework.redis.starter.key.builder.UserProviderCacheKeyBuilder;
import com.yangmao.live.user.constants.CacheAsyncDeleteCodeEnum;
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
    private RocketMQConsumerProperties consumerProperties;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    //这个和@postconstruct是一个效果
    @Override
    public void afterPropertiesSet() {
        initConsumer();
    }

    public void initConsumer() {
        try {
            //初始化我们的RocketMQ消费者
            DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
            defaultMQPushConsumer.setNamesrvAddr(consumerProperties.getNameSrv());
            defaultMQPushConsumer.setConsumerGroup(consumerProperties.getGroupName());
            defaultMQPushConsumer.setConsumeMessageBatchMaxSize(1);
            defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            defaultMQPushConsumer.subscribe(USER_CACHE_ASYNC_DELETE_TOPIC, "*");
            defaultMQPushConsumer.setMessageListener(
                    (MessageListenerConcurrently) (msgs, consumeConcurrentlyContext) -> {
                        String json = new String(msgs.get(0).getBody());
                        UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO =
                                JSON.parseObject(json,
                                        UserCacheAsyncDeleteDTO.class);
                        int code = userCacheAsyncDeleteDTO.getCode();
                        if (code == CacheAsyncDeleteCodeEnum.USER_INFO_DELETE.getCode()) {
                            Long userId =
                                    JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                            redisTemplate.delete(userProviderCacheKeyBuilder.buildUserInfoKey(
                                    userId));

                        } else if (code == CacheAsyncDeleteCodeEnum.USER_TAG_DELETE.getCode()) {
                            Long userId = JSON.parseObject(
                                            userCacheAsyncDeleteDTO.getJson())
                                    .getLong("userId");
                            redisTemplate.delete(userProviderCacheKeyBuilder.buildTagLockKey(
                                    userId));
                        }

                        return null;
                    }

            );
            defaultMQPushConsumer.start();
            LOGGER.info("mq消费者启动成功,nameSrv is {}", consumerProperties.getNameSrv());
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
    }

}
