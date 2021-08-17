package com.yinrj.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.yinrj.enums.OrderStatusEnum;
import lombok.Data;

/**
 * order_detail
 * @author 
 */
@Data
public class OrderDetail implements Serializable {
    /**
     * 订单id
     */
    private Integer id;

    /**
     * 状态
     */
    private OrderStatusEnum status;

    /**
     * 订单地址
     */
    private String address;

    /**
     * 用户id
     */
    private Integer accountId;

    /**
     * 产品id
     */
    private Integer productId;

    /**
     * 骑手id
     */
    private Integer deliverymanId;

    /**
     * 结算id
     */
    private Integer settlementId;

    /**
     * 积分奖励id
     */
    private Integer rewardId;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 时间
     */
    private Date date;

    private static final long serialVersionUID = 1L;
}