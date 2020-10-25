package com.harry.wechat.dto.server;

import lombok.Data;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 20:34
 * Desc: FriendDto
 */
@Data
public class FriendDto {
    private String wxid;
    private String wechatid;
    private String name;
    private String remark;
}
