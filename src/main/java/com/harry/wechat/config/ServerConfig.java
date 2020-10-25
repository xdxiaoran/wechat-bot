package com.harry.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Harry
 * @date 2020/10/22
 * Time: 23:38
 * Desc: ServerConfig
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "config.wechat")
public class ServerConfig {
    private String url;
}
