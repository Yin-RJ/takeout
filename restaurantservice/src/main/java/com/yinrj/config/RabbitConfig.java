package com.yinrj.config;

import com.yinrj.service.RestaurantMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/8/17
 */
@Configuration
public class RabbitConfig {
    @Autowired
    private RestaurantMessageService restaurantMessageService;

    @Autowired
    public void startListenMessage() {
        restaurantMessageService.handleMsg();
    }
}
