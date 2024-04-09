package com.yangmao.live.user.provider.dao.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangmao.live.user.provider.dao.po.UserPO;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-02
 */
@Mapper
public interface IUserMapper extends BaseMapper<UserPO> {

}
