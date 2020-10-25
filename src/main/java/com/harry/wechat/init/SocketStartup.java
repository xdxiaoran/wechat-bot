package com.harry.wechat.init;

import com.harry.wechat.config.ServerConfig;
import com.harry.wechat.service.WXServerListener;
import com.harry.wechat.util.SocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.enums.ReadyState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 01:48
 * Desc: SocketStartup
 */
// @Order(2)
// @Component
@Slf4j
public class SocketStartup implements ApplicationRunner {

    @Autowired
    private ServerConfig serverConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        WXServerListener client = new WXServerListener("ws://" + serverConfig.getUrl());
        client.setConnectionLostTimeout(-1);
        client.connect();
        while (!client.getReadyState().equals(ReadyState.OPEN)) {
            Thread.sleep(500);
            log.info("正在建立连接......");
        }
        log.info("连接成功,等待消息中");

        SocketProperties.client = client;
    }
}
