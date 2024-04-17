package com.yangmao.live.user.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;

import com.yangmao.live.user.constants.UserTagsEnum;
import com.yangmao.live.user.interfaces.IUserTagRpc;
import com.yangmao.live.user.provider.service.IUserTagService;

import jakarta.annotation.Resource;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-16
 */
@DubboService
public class UserTagRpcImpl implements IUserTagRpc {
    @Resource
    IUserTagService userTagService;
    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagService.setTag(userId,userTagsEnum);
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagService.cancelTag(userId,userTagsEnum);
    }

    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        return userTagService.containTag(userId,userTagsEnum);
    }
}
