package com.yinrj.config;

import com.yinrj.service.RewardMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author yinrongjie
 * @version 1.0
 * @date 2021/8/24
 * @description
 */
@Configuration
public class RabbitMQConfig {
    @Autowired
    private RewardMessageService service;

    @Autowired
    public void listenMessage() {
        service.handleMessage();
    }
}
