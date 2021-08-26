package com.yinrj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.yinrj.constant.ConfigConstant;
import com.yinrj.dto.OrderMessageDTO;
import com.yinrj.entity.OrderDetail;
import com.yinrj.enums.OrderStatusEnum;
import com.yinrj.mapper.OrderDetailDao;
import com.yinrj.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/8/17
 */
@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private ObjectMapper objectMapper;

    public void createOrder(OrderCreateVO orderCreateVO) throws IOException, TimeoutException, InterruptedException {
        // 保存数据库
        OrderDetail orderDetail = new OrderDetail();
        BeanUtils.copyProperties(orderCreateVO, orderDetail);
        orderDetail.setDate(new Date());
        orderDetail.setStatus(OrderStatusEnum.ORDER_CREATING);
        orderDetailDao.insert(orderDetail);
        // 给餐厅微服务发送消息
        OrderMessageDTO orderMessageDTO = new OrderMessageDTO();
        orderMessageDTO.setOrderId(orderDetail.getId());
        orderMessageDTO.setProductId(orderDetail.getProductId());
        orderMessageDTO.setAccountId(orderDetail.getAccountId());

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(ConfigConstant.MESSAGE_HOST);
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()){
            String msgToSend = objectMapper.writeValueAsString(orderMessageDTO);

            channel.confirmSelect();

            ConfirmListener confirmListener = new ConfirmListener() {
                /**
                 * 成功会被调用
                 * @param deliveryTag 发送端的消息序号
                 * @param multiple 多条消息还是单条消息
                 * @throws IOException
                 */
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    log.info("Ack, deliveryTag: {}, multiple: {}", deliveryTag, multiple);
                }

                /**
                 * 失败会被调用
                 * @param deliveryTag
                 * @param multiple
                 * @throws IOException
                 */
                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    log.info("Nack, deliveryTag: {}, multiple: {}", deliveryTag, multiple);
                }
            };
            channel.addConfirmListener(confirmListener);

            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("15000").build();

            channel.basicPublish("exchange.order.restaurant", "key.restaurant", null,
                    msgToSend.getBytes(StandardCharsets.UTF_8));
            log.info("order service sent msg to restaurant");

            Thread.sleep(10000l);

            if (channel.waitForConfirms()) {
                log.info("order service confirm success");
            } else {
                log.info("order service confirm failed");
            }
        }
    }
}
