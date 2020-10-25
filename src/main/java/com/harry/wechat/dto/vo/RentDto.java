package com.harry.wechat.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Harry
 * @date 2020/10/12
 * Time: 15:04
 * Desc: RentDto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentDto {
    private String wxid;
    private Long accountId;
    /**
     * 1: 租号
     * 2: 下号
     * 3: 全部下号
     */
    private int type;

    private String accountInfo;
}
