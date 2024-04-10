package com.yangmao.live.user.provider.rpc;

import java.util.List;
import java.util.Map;

import org.apache.dubbo.config.annotation.DubboService;

import com.yangmao.live.user.dto.UserDTO;
import com.yangmao.live.user.interfaces.IUserRpc;
import com.yangmao.live.user.provider.service.IUserService;

import jakarta.annotation.Resource;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-01
 */
@DubboService
public class UserRpcImpl implements IUserRpc {

    @Resource
    private IUserService userService;
    @Override
    public UserDTO getByUserId(Long userId) {
        return userService.getByUserId(userId);
    }

    @Override
    public Boolean updateUser(UserDTO userDTO) {return userService.updateUser(userDTO);}

    @Override
    public Boolean insertOne(UserDTO userDTO) {return userService.insertOne(userDTO);}

    @Override
    public Map<Long, UserDTO> batchGetUserInfo(List<Long> userIds) {return userService.batchGetUserInfo(userIds);}
}
