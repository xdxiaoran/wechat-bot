package com.harry.wechat;

import com.harry.wechat.config.ServerConfig;
import com.harry.wechat.service.WXServerListener;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.enums.ReadyState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.swing.*;

/**
 * @author Harry
 * @date 2020/10/22
 * Time: 22:14
 * Desc: WechatBotApplication
 */
@SpringBootApplication
@Slf4j
public class WechatBotApplication {
    @Autowired
    private ServerConfig serverConfig;

    public static String[] args;
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        WechatBotApplication.args = args;
        WechatBotApplication.context = SpringApplication.run(WechatBotApplication.class, args);
    }

    @Bean
    public WXServerListener getWXServerListener() throws Exception {
        // TODO: 2020/10/23  是否要做成bean ？
        WXServerListener client = new WXServerListener("ws://" + serverConfig.getUrl());
        client.setConnectionLostTimeout(-1);
        client.connect();
        int time = 10;
        while (!client.getReadyState().equals(ReadyState.OPEN) && time > 0) {
            Thread.sleep(500);
            log.info("正在建立连接......");
            time--;
        }
        if (time == 0) {
            JOptionPane.showMessageDialog(null, "微信服务端程序未启动！", "机器人启动失败",JOptionPane.INFORMATION_MESSAGE);
            context.close();
        }
        log.info("连接成功,等待消息中");
        return client;
    }
}
