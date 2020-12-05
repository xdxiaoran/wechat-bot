package com.harry.wechat.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 17:50
 * Desc: Account
 *
 * 关键词配置
 */
@Entity
@Data
public class Config extends BaseEntity {

    /**
     *
     */
    @Column(name = "k")
    private String key;
    /**
     * 1：回收
     * 2：全部回收
     * 3：查询语句
     * 4：黑名单
     */
    private String lab;
    @Column(name = "v")
    private String value;

}
