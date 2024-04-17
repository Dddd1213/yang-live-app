package com.yangmao.live.user.dto;

import java.io.Serial;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-16
 */
@Data
@Builder
public class UserCacheAsyncDeleteDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 不同业务场景的code，区别不同的延迟消息
     */
    private int code;
    private String json;
}
