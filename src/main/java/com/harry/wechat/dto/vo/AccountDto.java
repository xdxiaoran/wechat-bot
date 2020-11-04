package com.harry.wechat.dto.vo;

import com.harry.wechat.entity.Account;
import lombok.Data;

/**
 * @author Harry
 * @date 2020/11/3
 * Time: 00:03
 * Desc: AccountDto
 */
@Data
public class AccountDto extends Account {

    private Integer costTime;
    private String wxName;
}
