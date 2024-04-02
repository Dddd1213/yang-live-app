package com.yangmao.live.api.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yangmao.live.user.interfaces.IUserRpc;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-01
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @DubboReference(group = "test")
    private IUserRpc userRpc;

    @GetMapping("/dubbo")
    public String dubbo(){
        return userRpc.test();
    }
}
