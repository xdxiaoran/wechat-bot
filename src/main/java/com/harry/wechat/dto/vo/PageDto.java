package com.harry.wechat.dto.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Harry
 * @date 2020/10/25
 * Time: 14:27
 * Desc: PageDto
 */
@Data
public class PageDto<T> {
    private List<T> content;
    private long totalElements;
}
