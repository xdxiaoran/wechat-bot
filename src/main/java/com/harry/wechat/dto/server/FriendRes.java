package com.harry.wechat.dto.server;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 20:34
 * Desc: FriendRes
 */
@Data
public class FriendRes {
    private Integer code;
    @JSONField(name = "robot_wxid")
    private String robotWxid;
    private List<FriendDto> fdlist;
}
