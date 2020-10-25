package com.harry.wechat.dto.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Harry
 * @date 2020/10/24
 * Time: 18:58
 * Desc: RentCancelDto
 */
@Data
public class RentCancelDto {
    private List<Long> accountIds;
    private BigDecimal balance;
}
