package com.harry.wechat.dto.vo;

import com.harry.wechat.entity.Recharge;
import lombok.Data;

/**
 * @author Harry
 * @date 2020/10/25
 * Time: 14:23
 * Desc: RechargeInfoDto
 */
@Data
public class RechargeInfoDto extends Recharge {

    private String nickName;
    private String remarkName;
}
