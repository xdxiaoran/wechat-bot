package com.harry.wechat.entity;

import com.harry.wechat.dto.server.FriendDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * @author Harry
 * @date 2020/9/28
 * Time: 17:10
 * Desc: UserInfo
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo extends BaseEntity {
    /**
     * uid
     */
    private String wxid;
    private String nickName;
    private String remarkName;
    private String mark;
    /**
     * 0: 普通用户
     * 1: vip用户
     * 2: svip
     */
    private Integer type;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 用户状态
     * 0 正常
     * 1 异常(不能租号)
     */
    private Integer status;

    /**
     * 租号模式
     * 1 先打后付款
     * 2 先付款后打
     */
    private Integer rentMode;

    /**
     * 提示时间
     * 默认 6 小时 360
     * 单位 分
     */
    private Integer remindTime;


    public static UserInfo of(FriendDto dto){
        return UserInfo.builder()
                .nickName(dto.getName())
                .remarkName(dto.getRemark())
                .wxid(dto.getWxid())
                .balance(BigDecimal.ZERO)
                .status(0)
                .status(0)
                .type(0)
                .rentMode(1)
                .remindTime(360)
                .build();
    }
}
