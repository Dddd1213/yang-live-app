package com.yangmao.live.user.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;

import com.yangmao.live.user.interfaces.IUserRpc;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-01
 */
@DubboService
public class UserRpcImpl implements IUserRpc {
    @Override
    public String test() {
        System.out.println("hello world!");
        return "success";
    }
}
