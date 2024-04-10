package com.yangmao.live.framework.redis.starter.key.builder;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Conditional;

import com.yangmao.live.framework.redis.starter.key.RedisKeyBuilder;
import com.yangmao.live.framework.redis.starter.key.RedisKeyLoadMatch;

@Configurable
@Conditional(RedisKeyLoadMatch.class)
public class OtherCacheKeyBuilder extends RedisKeyBuilder {

    private static String USER_INFO_KEY = "other";

    public String buildUserInfoKey(Long userId) {
        return super.getPrefix() + USER_INFO_KEY + super.getSplitItem() + userId;
    }

}
