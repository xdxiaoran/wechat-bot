package com.harry.wechat.dto.server;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Harry
 * @date 2020/10/22
 * Time: 23:46
 * Desc: Instruction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instruction {

    private Integer funid;

    @JSONField(name = "WeChatID")
    private Integer WeChatID;
    private String wxid;
    private String content;
    private String transferid;

    public static Instruction of(String wxid, String content) {
        return builder().wxid(wxid).content(content).build();
    }

    public static Instruction of(String wxid, String content,Integer funid) {
        return builder().wxid(wxid).content(content).funid(funid).build();
    }

    public static Instruction of(Integer funid) {
        return builder().funid(funid).build();
    }
}
