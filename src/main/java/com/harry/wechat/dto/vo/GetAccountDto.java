package com.harry.wechat.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 18:06
 * Desc: GetAccountDto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAccountDto {

    private List<String> servers;

    /**
     * 0 单双排位
     * 1 灵活排位
     * 2 云顶之弈
     */
    private List<String> modes;

    private Set<Integer> levelIndex;

    private List<String> heros;

    private List<String> skins;

    private String wxid;

    private String accountName;

}
