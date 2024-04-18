package com.yangmao.live.user.provider.service.impl;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.yangmao.live.user.constants.UserTagsEnum;
import com.yangmao.live.user.dto.UserDTO;
import com.yangmao.live.user.provider.service.IUserService;
import com.yangmao.live.user.provider.service.IUserTagService;

import jakarta.annotation.Resource;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-17
 */
@SpringBootTest
class UserTagServiceImplTest {

    @Resource
    private IUserTagService userTagService;
    @Resource
    private IUserService userService;

    @Test
    void setTag() {
        UserDTO build = UserDTO.builder()
                .userId(1L)
                .bornCity(3)
                .build();
        userService.updateUser(build);
    }

    @Test
    void cancelTag() {
                Long userId = 1001L;
                System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_VIP));
                System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_OLD_USER));
                System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_RICH));
                System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_VIP));
                System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_OLD_USER));
                System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
    }

    @Test
    void containTag() {
    }
}