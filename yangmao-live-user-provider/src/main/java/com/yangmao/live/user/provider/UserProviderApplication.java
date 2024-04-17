package com.yangmao.live.user.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.yangmao.live.user.provider.service.IUserService;
import com.yangmao.live.user.provider.service.IUserTagService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-01
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@Slf4j
public class UserProviderApplication implements CommandLineRunner {

    @Resource
    private IUserTagService userTagService;
    @Resource
    private IUserService userService;

    public static void main(String[] args) {
//        SpringApplication.run(UserProviderApplication.class, args);
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        //用不到tomcat
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
//        Long userId = 1001L;
//        System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_VIP));
//        System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_OLD_USER));
//        System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_RICH));
//        System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_VIP));
//        System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_OLD_USER));
//        System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));

//        Long userId = 1002L;
//        for(int i=0;i<100;i++){
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("result is " + userTagService.setTag(userId, UserTagsEnum.IS_VIP));
//                }
//            });
//            thread.start();
//        }
        userService.getByUserId(1L);
        userService.getByUserId(2L);
    }
}
