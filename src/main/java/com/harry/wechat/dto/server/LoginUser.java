package com.harry.wechat.dto.server;

import lombok.Builder;
import lombok.Data;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 00:44
 * Desc: LoginUser
 */
@Data
@Builder
public class LoginUser {

    private String name;
    /**
     * 微信号
     */
    private String account;
    private String phone;
    private String wxid;
    /**
     * url
     */
    private String photo;

    private Integer wechatId;

    public static LoginUser of(AccountInfo info, Integer wechatId) {
        return LoginUser.builder()
                .wechatId(wechatId)
                .name(info.getName())
                .account(info.getAccount())
                .phone(info.getPhone())
                .wxid(info.getWxid())
                .build();
    }
}
