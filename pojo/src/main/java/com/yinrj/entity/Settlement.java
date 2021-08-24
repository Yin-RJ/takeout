package com.yinrj.entity;

import com.yinrj.enums.SettlementStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * settlement
 * @author 
 */
@Data
public class Settlement implements Serializable {
    /**
     * 结算id
     */
    private Integer id;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 交易id
     */
    private Integer transactionId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 状态
     */
    private SettlementStatusEnum status;

    /**
     * 时间
     */
    private Date date;

    private static final long serialVersionUID = 1L;
}