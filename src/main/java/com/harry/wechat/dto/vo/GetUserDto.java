package com.harry.wechat.dto.vo;

import lombok.Data;

/**
 * @author Harry
 * @date 2020/10/9
 * Time: 13:51
 * Desc: GetUserDto
 */
@Data
public class GetUserDto {
    private String name;
    private String mark;
    private Integer sex;
    private String status;
    private Integer page;
    private Integer size;
}
