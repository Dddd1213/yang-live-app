package com.yangmao.live.user.interfaces;

import java.util.List;
import java.util.Map;

import com.yangmao.live.user.dto.UserDTO;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-01
 */
public interface IUserRpc {
    UserDTO getByUserId(Long userId);

    Boolean updateUser(UserDTO userDTO);

    Boolean insertOne(UserDTO userDTO);

    Map<Long,UserDTO> batchGetUserInfo(List<Long> userIds);

}
