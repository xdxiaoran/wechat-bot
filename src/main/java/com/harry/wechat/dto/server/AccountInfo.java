package com.harry.wechat.dto.server;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 00:38
 * Desc: AccountInfo
 */
@Data
public class AccountInfo {
    @JSONField(name = "名称")
    private String name;
    @JSONField(name = "型号")
    private String device;
    @JSONField(name = "微信号")
    private String account;
    @JSONField(name = "手机号")
    private String phone;
    @JSONField(name = "微信ID")
    private String wxid;
    @JSONField(name = "头像地址")
    private String photo;
}
