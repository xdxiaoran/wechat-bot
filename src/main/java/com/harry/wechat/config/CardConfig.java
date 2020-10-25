package com.harry.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Harry
 * @date 2020/10/25
 * Time: 00:35
 * Desc: CardConfig
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "card")
public class CardConfig {
    private Boolean mode;
    private String content;
    private String bigheadimgurl;
    private String smallheadimgurl;
    private String username;
    private String nickname;
    private String autoword;

}
