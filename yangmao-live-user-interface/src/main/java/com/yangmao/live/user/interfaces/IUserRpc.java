package com.yangmao.live.user.interfaces;

import com.yangmao.live.user.dto.UserDTO;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-01
 */
public interface IUserRpc {
    UserDTO getByUserId(Long userId);

    Boolean updateUser(UserDTO userDTO);

    Boolean insertOne(UserDTO userDTO);

}
