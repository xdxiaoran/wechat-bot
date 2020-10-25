package com.harry.wechat.dto.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Harry
 * @date 2020/10/13
 * Time: 00:29
 * Desc: OrderResponseDto
 */
@Data
public class OrderResponseDto {
    private String message;

    private BigDecimal amount;
}
