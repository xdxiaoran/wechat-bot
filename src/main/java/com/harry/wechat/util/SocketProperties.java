package com.harry.wechat.util;

import com.harry.wechat.dto.server.BaseRes;
import com.harry.wechat.service.WXServerListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 01:51
 * Desc: SocketProperties
 */
@Slf4j
public class SocketProperties {

    public static WXServerListener client;

    public static volatile BlockingQueue<BaseRes> messages = new LinkedBlockingQueue<>();


    /**
     * 休眠，单位: 毫秒
     *
     * @param ms
     */
    public static void sleep(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            log.error("", e);
        }
    }
}
