package com.harry.wechat.dto.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harry.wechat.config.MsgType;
import lombok.Data;
import lombok.ToString;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 10:56
 * Desc: BaseRes
 */
@Data
@ToString
public class BaseRes {
    /**
     * 消息id
     */
    private Integer id;
    /**
     * 发送人id
     */
    private String wxid;
    /**
     * 当前账户id
     */
    private String robot_wxid;
    private String content;
    private Integer type;
    private String attach;
    private Integer attach2;
    @JsonProperty("WeChatID")
    private String wechatId;


    public MsgType msgType() {
        switch (this.type) {
            case 1:
                return MsgType.TEXT;
            case 3:
                return MsgType.IMAGE;
            case 34:
                return MsgType.VOICE;
            case 37:
                return MsgType.ADD_FRIEND;
            case 42:
                return MsgType.PERSON_CARD;
            case 43:
                return MsgType.VIDEO;
            case 47:
                return MsgType.EMOTICONS;
            case 49:
                return MsgType.TRANSFER;
            case 51:
                return MsgType.CONTACT_INIT;
            case 62:
                return MsgType.VIDEO;
            case 10000:
                return MsgType.SYSTEM;
            case 10002:
                return MsgType.REVOKE_MSG;
            default:
                return MsgType.UNKNOWN;
        }
    }

}
