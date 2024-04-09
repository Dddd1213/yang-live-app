package com.yangmao.live.user.provider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yangmao.live.user.dto.UserDTO;
import com.yangmao.live.user.provider.dao.po.UserPO;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-02
 */
public interface IUserService extends IService<UserPO> {

    UserDTO getByUserId(Long userId);

    Boolean updateUser(UserDTO userDTO);

    Boolean insertOne(UserDTO userDTO);

}
