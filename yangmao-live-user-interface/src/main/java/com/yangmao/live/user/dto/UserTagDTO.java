package com.yangmao.live.user.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserTagDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long tagInfo01;

    private Long tagInfo02;

    private Long tagInfo03;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
