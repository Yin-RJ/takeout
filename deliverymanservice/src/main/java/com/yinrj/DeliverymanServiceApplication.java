package com.yinrj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/8/17
 */
@SpringBootApplication
@MapperScan("com.yinrj")
public class DeliverymanServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliverymanServiceApplication.class, args);
    }
}
