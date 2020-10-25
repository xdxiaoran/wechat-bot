package com.harry.wechat.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 17:50
 * Desc: Account
 */
@Entity
@Data
public class Account extends BaseEntity {

    private String username;
    private String password;
    private String nickName;
    private String server;
    private String level;
    private Integer creditScore;
    private String rankLevelSingle;
    private Integer rankIndexSingle;
    private String rankLevelFlexible;
    private Integer rankIndexFlexible;
    private String chessLevel;
    @Column(name = "hero_list", columnDefinition = "text")
    private String heroList;
    private Integer heroNum;
    @Column(name = "skin_list", columnDefinition = "text")
    private String skinList;
    private Integer skinNum;
    /**
     * 价格 单位
     */
    private String price;
    private int status;
    /**
     * 账号级别
     * @see UserInfo#getType()
     */
    private Integer vipLevel;
}
