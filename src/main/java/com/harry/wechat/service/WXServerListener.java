package com.harry.wechat.service;

import com.alibaba.fastjson.JSON;
import com.harry.wechat.WechatBotApplication;
import com.harry.wechat.dto.server.BaseRes;
import com.harry.wechat.util.SocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.boot.SpringApplication;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Harry
 * @date 2020/10/22
 * Time: 22:16
 * Desc: WXServerListener
 */
@Slf4j
public class WXServerListener extends WebSocketClient {

    public WXServerListener(String url) throws URISyntaxException {
        super(new URI(url));
    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }

    @Override
    public void onMessage(String s) {
        // log.info(s);
         SocketProperties.messages.add(JSON.parseObject(s, BaseRes.class));
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("断开连接！");
        //重启客户端
        restartListener();
    }

    @Override
    public void onError(Exception e) {

    }

    /**
     * Spring重启，实现客户端的自动重连
     */
    public void restartListener() {
        ExecutorService threadPool = new ThreadPoolExecutor(1, 1, 0,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute(() -> {
            WechatBotApplication.context.close();
            WechatBotApplication.context = SpringApplication.run(WechatBotApplication.class,
                    WechatBotApplication.args);
        });
        threadPool.shutdown();

    }
}
