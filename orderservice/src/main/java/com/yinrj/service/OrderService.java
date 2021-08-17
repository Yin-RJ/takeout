package com.yinrj.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.yinrj.constant.ConfigConstant;
import com.yinrj.dto.OrderMessageDTO;
import com.yinrj.entity.OrderDetail;
import com.yinrj.enums.OrderStatusEnum;
import com.yinrj.mapper.OrderDetailDao;
import com.yinrj.vo.OrderCreateVO;
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
public class OrderService {
    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private ObjectMapper objectMapper;

    public void createOrder(OrderCreateVO orderCreateVO) throws IOException, TimeoutException {
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
            channel.basicPublish("exchange.order.restaurant", "key.restaurant", null, msgToSend.getBytes(StandardCharsets.UTF_8));
        }
    }
}
