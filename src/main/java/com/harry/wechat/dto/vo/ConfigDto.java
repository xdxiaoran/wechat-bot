package com.harry.wechat.dto.vo;

import lombok.Data;

/**
 * @author Harry
 * @date 2020/11/23
 * Time: 22:33
 * Desc: ConfigDto
 */
@Data
public class ConfigDto {

    private Long id;

    /**
     *
     */
    private String key;
    /**
     * 1：回收
     * 2：全部回收
     * 3：查询语句
     * 4：黑名单
     */
    private String lab;
    private String value;
}
