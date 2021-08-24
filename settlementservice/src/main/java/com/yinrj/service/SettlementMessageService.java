package com.yinrj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.yinrj.constant.ConfigConstant;
import com.yinrj.dto.OrderMessageDTO;
import com.yinrj.entity.Settlement;
import com.yinrj.enums.SettlementStatusEnum;
import com.yinrj.mapper.SettlementDao;
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
public class SettlementMessageService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private SettlementDao settlementDao;

    DeliverCallback deliverCallback = (((consumerTag, message) -> {
        String msg = new String(message.getBody());

        log.info("settlement service receive message: {}", msg);

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ConfigConstant.MESSAGE_HOST);

        try (Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel()){
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(msg.getBytes(StandardCharsets.UTF_8),
                    OrderMessageDTO.class);

            Settlement settlement = new Settlement();
            settlement.setAmount(orderMessageDTO.getPrice());
            settlement.setDate(new Date());
            settlement.setOrderId(orderMessageDTO.getOrderId());
            int settlementId = settlementService.settlement(orderMessageDTO.getAccountId(), orderMessageDTO.getPrice());
            settlement.setTransactionId(settlementId);
            settlement.setStatus(SettlementStatusEnum.SUCCESS);
            settlementDao.insert(settlement);

            orderMessageDTO.setSettlementId(settlementId);
            String messageToSend = objectMapper.writeValueAsString(orderMessageDTO);
            channel.basicPublish("exchange.settlement.order", "key.order",
             null, messageToSend.getBytes(StandardCharsets.UTF_8));
        } catch (TimeoutException e) {
            log.error(e.getMessage(), e);
        }
    }));

    @Async
    public void handleMessage() {
        log.info("settlement service start listening message");
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ConfigConstant.MESSAGE_HOST);
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare("queue.settlement", true, false, false, null);

            channel.exchangeDeclare("exchange.order.settlement", BuiltinExchangeType.FANOUT, true, false, null);
            channel.exchangeDeclare("exchange.settlement.order", BuiltinExchangeType.FANOUT, true, false, null);
            channel.queueBind("queue.settlement", "exchange.order.settlement", "key.settlement");

            channel.basicConsume("queue.settlement", true, deliverCallback, consumerTag -> {});

            while (true) {
                Thread.sleep(1000000l);
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
