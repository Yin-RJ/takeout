package com.yinrj.dto;

import com.yinrj.enums.OrderStatusEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/8/17
 */
@Data
public class OrderMessageDTO {
    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 订单状态
     */
    private OrderStatusEnum orderStatus;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 骑手ID
     */
    private Integer deliverymanId;

    /**
     * 产品ID
     */
    private Integer productId;

    /**
     * 用户ID
     */
    private Integer accountId;

    /**
     * 结算ID
     */
    private Integer settlementId;

    /**
     * 积分ID
     */
    private Integer rewardId;

    /**
     * 积分数量
     */
    private BigDecimal rewardAmount;

    /**
     * 确认
     */
    private Boolean confirmed;
}
