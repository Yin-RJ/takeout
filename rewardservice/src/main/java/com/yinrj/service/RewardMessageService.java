package com.yinrj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.yinrj.constant.ConfigConstant;
import com.yinrj.dto.OrderMessageDTO;
import com.yinrj.entity.Reward;
import com.yinrj.enums.RewardStatusEnum;
import com.yinrj.mapper.RewardDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * @author yinrongjie
 * @version 1.0
 * @date 2021/8/24
 * @description
 */
@Service
@Slf4j
public class RewardMessageService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RewardDao rewardDao;

    @Async
    public void handleMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ConfigConstant.MESSAGE_HOST);

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare("exchange.order.reward", BuiltinExchangeType.TOPIC, true, false, false, null);
            channel.queueDeclare("queue.reward", true, false, false, null);

            channel.queueBind("queue.reward", "exchange.order.reward", "key.reward");

            channel.basicConsume("queue.reward", true, deliverCallback, consumerTag -> {});

            while (true) {
                Thread.sleep(10000000000l);
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    DeliverCallback deliverCallback = (((consumerTag, message) -> {
        String msg = new String(message.getBody());
        log.info("reward receive msg: {}", msg);
        OrderMessageDTO orderMessageDTO = objectMapper.readValue(msg.getBytes(StandardCharsets.UTF_8),
                OrderMessageDTO.class);
        Reward reward = new Reward();
        reward.setDate(new Date());
        reward.setAmount(orderMessageDTO.getPrice());
        reward.setOrderId(orderMessageDTO.getOrderId());
        reward.setStatus(RewardStatusEnum.SUCCESS);
        rewardDao.insert(reward);

        orderMessageDTO.setRewardId(reward.getId());
        orderMessageDTO.setRewardAmount(reward.getAmount());

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ConfigConstant.MESSAGE_HOST);

        try (Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()) {
            String msgToSend = objectMapper.writeValueAsString(orderMessageDTO);

            channel.basicPublish("exchange.order.reward", "key.order", null, msgToSend.getBytes(StandardCharsets.UTF_8));
        } catch (TimeoutException e) {
            log.error(e.getMessage(), e);
        }
    }));
}
