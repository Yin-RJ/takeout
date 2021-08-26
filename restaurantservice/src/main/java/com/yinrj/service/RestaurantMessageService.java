package com.yinrj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.yinrj.constant.ConfigConstant;
import com.yinrj.dto.OrderMessageDTO;
import com.yinrj.entity.Product;
import com.yinrj.entity.Restaurant;
import com.yinrj.enums.ProductStatusEnum;
import com.yinrj.enums.RestaurantStatusEnum;
import com.yinrj.mapper.ProductDao;
import com.yinrj.mapper.RestaurantDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/8/17
 */
@Slf4j
@Service
public class RestaurantMessageService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private RestaurantDao restaurantDao;

    @Async
    public void handleMsg() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ConfigConstant.MESSAGE_HOST);
        connectionFactory.setHandshakeTimeout(ConfigConstant.HANDSHAKE_TIMEOUT);

        try (Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()){

            channel.exchangeDeclare("exchange.order.restaurant", BuiltinExchangeType.DIRECT, true, false, null);

            channel.queueDeclare("queue.restaurant", true, false, false, null);

            channel.queueBind("queue.restaurant", "exchange.order.restaurant", "key.restaurant");

            channel.basicConsume("queue.restaurant", true, deliverCallback, consumerTag -> {});
            while (true) {
                Thread.sleep(1000000l);
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    DeliverCallback deliverCallback = (((consumerTag, message) -> {
        String msg = new String(message.getBody());
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ConfigConstant.MESSAGE_HOST);
        factory.setHandshakeTimeout(ConfigConstant.HANDSHAKE_TIMEOUT);

        // 走出try块的时候connection和channel都会被关掉
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(msg, OrderMessageDTO.class);

            Product product = productDao.selectByPrimaryKey(orderMessageDTO.getProductId());
            if (product == null) {
                throw new RuntimeException("product is not exist");
            }
            Restaurant restaurant = restaurantDao.selectByPrimaryKey(product.getRestaurantId());
            if (restaurant == null) {
                throw new RuntimeException("restaurant is not exist");
            }

            if (product.getStatus() == ProductStatusEnum.AVAILABLE && restaurant.getStatus() == RestaurantStatusEnum.OPEN) {
                orderMessageDTO.setConfirmed(true);
                orderMessageDTO.setPrice(product.getPrice());
            } else {
                orderMessageDTO.setConfirmed(false);
            }

            channel.addReturnListener(new ReturnCallback() {
                @Override
                public void handle(Return returnMessage) {
                    log.info("Message return: {}", returnMessage);
                }
            });

            String msgToSend = objectMapper.writeValueAsString(orderMessageDTO);
            // mandatory为true的时候表示开启了消息返回机制，如果没有路由到相应的队列，则会调用回调函数ReturnListener
            channel.basicPublish("exchange.order.restaurant", "key.order", true, null,
                    msgToSend.getBytes(StandardCharsets.UTF_8));

            Thread.sleep(1000);


        } catch (TimeoutException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }));
}
