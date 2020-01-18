package com.goku.order.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author fuyongde
 * @date 2020/1/18 15:36
 */
@Data
public class Order {
    private Long id;
    private Long userId;
    private String mobile;
    private String province;
    private String city;
    private String area;
    private String address;
    private Date createTime;
    private Date updateTime;
}
