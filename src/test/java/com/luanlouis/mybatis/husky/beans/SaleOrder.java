package com.luanlouis.mybatis.husky.beans;

import lombok.Data;

import java.util.Date;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author terrylouis1991@gmail.com
 * @date 2019/04/09
 */
@Data
public class SaleOrder {

    private String orderId;

    private String tenantId;

    private String sku;

    private String commodityName;

    private String buyer;

    private Date orderTime;

}
