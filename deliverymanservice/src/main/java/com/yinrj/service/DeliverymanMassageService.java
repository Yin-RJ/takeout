package com.yinrj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.yinrj.constant.ConfigConstant;
import com.yinrj.dto.OrderMessageDTO;
import com.yinrj.entity.Deliveryman;
import com.yinrj.entity.DeliverymanExample;
import com.yinrj.enums.DeliverymanStatusEnum;
import com.yinrj.mapper.DeliverymanDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/8/17
 */
@Service
@Slf4j
public class DeliverymanMassageService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeliverymanDao deliverymanDao;

    @Async
    public void handleMessage() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ConfigConstant.MESSAGE_HOST);
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()){

            channel.exchangeDeclare("exchange.order.deliveryman", BuiltinExchangeType.DIRECT, true, false, null);
            channel.queueDeclare("queue.deliveryman", true, false, false, null);
            channel.queueBind("queue.deliveryman", "exchange.order.deliveryman", "key.deliveryman");

            channel.basicConsume("queue.deliveryman", true, deliverCallback, consumerTag -> {});
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

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(msg, OrderMessageDTO.class);
            List<Deliveryman> deliverymenList = getAllAvailableDeliveryman();
            orderMessageDTO.setDeliverymanId(deliverymenList.get(0).getId());



            String msgToSend = objectMapper.writeValueAsString(orderMessageDTO);
            channel.basicPublish("exchange.order.deliveryman", "key.order", null, msgToSend.getBytes(StandardCharsets.UTF_8));


        } catch (TimeoutException e) {
            log.error(e.getMessage(), e);
        }
    }));

    private List<Deliveryman> getAllAvailableDeliveryman() {
        DeliverymanExample example = new DeliverymanExample();
        DeliverymanExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(DeliverymanStatusEnum.AVAILABLE.toString());
        return deliverymanDao.selectByExample(example);
    }

}
