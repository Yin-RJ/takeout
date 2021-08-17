package com.yinrj.vo;

import lombok.Data;

/**
 * @author yinrongjie
 * @version 1.0
 * @description
 * @date 2021/8/17
 */
@Data
public class OrderCreateVO {
    /**
     * 用户ID
     */
    private Integer accountId;
    /**
     * 地址
     */
    private String address;
    /**
     * 产品ID
     */
    private Integer productId;
}
