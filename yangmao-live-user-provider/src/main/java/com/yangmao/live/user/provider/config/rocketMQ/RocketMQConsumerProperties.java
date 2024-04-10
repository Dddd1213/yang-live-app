package com.yangmao.live.user.provider.config.rocketMQ;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-10
 */
@ConfigurationProperties(prefix = "yangmao.rmq.consumer")
@Configuration
@Data
public class RocketMQConsumerProperties {
    //rocketmq的nameSever地址
    private String nameSrv;
    //分组名称
    private String groupName;

}
