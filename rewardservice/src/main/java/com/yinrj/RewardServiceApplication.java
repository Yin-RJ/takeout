package com.yinrj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yinrongjie
 * @version 1.0
 * @date 2021/8/24
 * @description
 */
@SpringBootApplication
@MapperScan("com.yinrj")
public class RewardServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RewardServiceApplication.class, args);
    }
}
