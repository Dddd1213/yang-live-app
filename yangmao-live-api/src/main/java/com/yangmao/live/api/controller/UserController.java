package com.yangmao.live.api.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yangmao.live.user.dto.UserDTO;
import com.yangmao.live.user.interfaces.IUserRpc;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-01
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @DubboReference()
    private IUserRpc userRpc;

    @GetMapping("/getUserInfo")
    public UserDTO getUserInfo(Long userId){
        return userRpc.getByUserId(userId);
    }

    @PostMapping("/updateUserInfo")
    public Boolean updateUserInfo(@RequestBody UserDTO userDTO){
        return userRpc.updateUser(userDTO);
    }

    @PostMapping("/insert")
    public Boolean insertUser(@RequestBody UserDTO userDTO){return userRpc.insertOne(userDTO);}
}
