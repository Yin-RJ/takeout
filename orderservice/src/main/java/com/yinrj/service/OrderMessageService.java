package com.yinrj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.yinrj.constant.ConfigConstant;
import com.yinrj.dto.OrderMessageDTO;
import com.yinrj.entity.OrderDetail;
import com.yinrj.enums.OrderStatusEnum;
import com.yinrj.mapper.OrderDetailDao;
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
 * @description 和消息处理相关的业务处理逻辑
 * @date 2021/8/17
 */
@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderDetailDao orderDetailDao;
    /**
     * 声明消息队列、交换机、绑定、消息的处理
     */
    @Async
    public void handleMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ConfigConstant.MESSAGE_HOST);

        // 保证订单微服务能够收到其他微服务发来的消息
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            // 商家微服务和订单微服务之间的交换机
            channel.exchangeDeclare("exchange.order.restaurant", BuiltinExchangeType.DIRECT, true, false, null);

            // 声明自己监听的队列，也就是订单微服务监听的队列，在哪个队列上收消息声明哪个队列
            channel.queueDeclare("queue.order", true, false, false, null);

            channel.queueBind("queue.order", "exchange.order.restaurant", "key.order");


            // 骑手微服务和订单微服务之间的交换机
            channel.exchangeDeclare("exchange.order.deliveryman", BuiltinExchangeType.DIRECT, true, false, null);

            channel.queueBind("queue.order", "exchange.order.deliveryman", "key.order");

            // 进入监听状态
            channel.basicConsume("queue.order", true, deliverCallback, consumerTag -> {});

            while (true) {
                Thread.sleep(10000000l);
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息送达时的通知回调接口
     */
    DeliverCallback deliverCallback = (((consumerTag, message) -> {
        String messageBody = new String(message.getBody());

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ConfigConstant.MESSAGE_HOST);

        try {
            // 将消息体反序列化成DTO
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(messageBody.getBytes(StandardCharsets.UTF_8), OrderMessageDTO.class);
            OrderDetail orderDetail = orderDetailDao.selectByPrimaryKey(orderMessageDTO.getOrderId());

            switch (orderDetail.getStatus()) {
                case ORDER_CREATING:
                    if (orderMessageDTO.getConfirmed() && orderMessageDTO.getPrice() != null) {
                        orderDetail.setStatus(OrderStatusEnum.RESTAURANT_CONFIRM);
                        orderDetail.setPrice(orderMessageDTO.getPrice());
                        orderDetailDao.updateByPrimaryKeySelective(orderDetail);
                        try (Connection connection = connectionFactory.newConnection();
                            Channel channel = connection.createChannel()){
                            String msgToSend = objectMapper.writeValueAsString(orderMessageDTO);
                            channel.basicPublish("exchange.order.deliveryman", "key.deliveryman", null, msgToSend.getBytes(StandardCharsets.UTF_8));
                        }
                    } else {
                        orderDetail.setStatus(OrderStatusEnum.ORDER_FILE);
                        orderDetailDao.updateByPrimaryKeySelective(orderDetail);
                    }
                    break;
                case RESTAURANT_CONFIRM:
                    break;
                case DELIVERYMAN_CONFIRM:
                    break;
                case SETTLEMENT_CONFIRM:
                    break;
                case ORDER_CREATED:
                    break;
                case ORDER_FILE:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }));
}
