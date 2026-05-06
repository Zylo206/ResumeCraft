package com.zylo.resumecraft.dto;

import lombok.Data;

@Data
public class CouponQuoteDTO {
    private Integer listPrice;
    private Integer discountAmount;
    private Integer payableAmount;
    private String couponStatus;
    private boolean paymentEnabled;
}

