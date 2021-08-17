package com.yinrj.entity;

import com.yinrj.enums.RestaurantStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * restaurant
 * @author 
 */
@Data
public class Restaurant implements Serializable {
    /**
     * 餐厅id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 状态
     */
    private RestaurantStatusEnum status;

    /**
     * 结算id
     */
    private Integer settlementId;

    /**
     * 时间
     */
    private Date date;

    private static final long serialVersionUID = 1L;
}