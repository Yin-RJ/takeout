package com.yinrj.enums;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/8/17
 */
public enum OrderStatusEnum {
    /**
     * 创建中
     */
    ORDER_CREATING,

    /**
     * 餐厅已确认
     */
    RESTAURANT_CONFIRM,

    /**
     * 骑手已确认
     */
    DELIVERYMAN_CONFIRM,

    /**
     * 已结算
     */
    SETTLEMENT_CONFIRM,

    /**
     * 订单已创建
     */
    ORDER_CREATED,

    /**
     * 订单创建失败
     */
    ORDER_FILE
}
