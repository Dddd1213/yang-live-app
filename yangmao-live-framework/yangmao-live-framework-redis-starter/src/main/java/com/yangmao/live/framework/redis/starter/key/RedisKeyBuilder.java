package com.yangmao.live.framework.redis.starter.key;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-09
 */
public class RedisKeyBuilder {

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String SPLIT_ITEM = ":";

    public String getSplitItem() {
        return SPLIT_ITEM;
    }

    public String getPrefix() {
        return applicationName + SPLIT_ITEM;
    }

}
