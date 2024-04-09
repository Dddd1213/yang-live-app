package com.yangmao.live.user.provider.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangmao.live.common.ConvertBeanUtils;
import com.yangmao.live.user.dto.UserDTO;
import com.yangmao.live.user.provider.dao.mapper.IUserMapper;
import com.yangmao.live.user.provider.dao.po.UserPO;
import com.yangmao.live.user.provider.service.IUserService;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<IUserMapper, UserPO> implements IUserService {
    @Override
    public UserDTO getByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return ConvertBeanUtils.convert(this.getById(userId), UserDTO.class);
    }

    @Override
    public Boolean updateUser(UserDTO userDTO) {
        if(userDTO == null || userDTO.getUserId()==null){
            return false;
        }
        return this.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
    }

    @Override
    public Boolean insertOne(UserDTO userDTO) {
        if(userDTO == null|| userDTO.getUserId()==null){
            return false;
        }
        return this.save(ConvertBeanUtils.convert(userDTO, UserPO.class));
    }
}
