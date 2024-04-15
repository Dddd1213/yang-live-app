package com.yangmao.live.id.generate.provider;

import java.util.HashSet;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.yangmao.live.id.generate.provider.service.IIdGenerateService;

import jakarta.annotation.Resource;

/**
 * @author daichenyang <daichenyang@kuaishou.com>
 * Created on 2024-04-10
 */
@EnableDubbo
@SpringBootApplication
@EnableDiscoveryClient
public class IdGenerateApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerateApplication.class);

    @Resource
    private IIdGenerateService idGenerateService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(IdGenerateApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Override
    public void run(String... args){
        HashSet<Long> idSet = new HashSet<>();
        for(int i=0;i<1000;i++){
            Long seqId = idGenerateService.getUnSeqId(1);
            System.out.println(seqId);
            idSet.add(seqId);
        }
        System.out.println(idSet.size());
    }

}
