package com.harry.wechat.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Harry
 * @date 2020/10/14
 * Time: 10:45
 * Desc: RechargeDto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeDto {
    private Long userId;
    private BigDecimal balance;
    /**
     * 充值方式
     * 0 转账
     * 1 后台（红包）
     */
    private Integer type;
}
