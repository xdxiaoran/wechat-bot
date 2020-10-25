package com.harry.wechat.dto.server;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 00:37
 * Desc: LoginDto
 */
@Data
public class LoginDto {

    @JSONField(name = "WeChatID")
    private Integer WeChatID;
    private AccountInfo data;
}
