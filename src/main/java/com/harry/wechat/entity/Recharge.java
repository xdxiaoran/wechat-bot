package com.harry.wechat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * @author Harry
 * @date 2020/10/14
 * Time: 10:41
 * Desc: Recharge
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recharge extends BaseEntity {

    private Long userId;
    private BigDecimal balance;

    /**
     * 充值方式
     * 0 转账
     * 1 后台（红包）
     */
    private Integer type;
}
