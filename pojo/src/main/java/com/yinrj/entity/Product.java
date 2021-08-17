package com.yinrj.entity;

import com.yinrj.enums.ProductStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * product
 * @author 
 */
@Data
public class Product implements Serializable {
    /**
     * 产品id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 地址
     */
    private Integer restaurantId;

    /**
     * 状态
     */
    private ProductStatusEnum status;

    /**
     * 时间
     */
    private Date date;

    private static final long serialVersionUID = 1L;
}