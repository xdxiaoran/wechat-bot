package com.harry.wechat.init;

import com.harry.wechat.dto.server.BaseRes;
import com.harry.wechat.service.WeChatervice;
import com.harry.wechat.util.InstructionUtil;
import com.harry.wechat.util.SocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 11:59
 * Desc: MessageConsumer
 */
@Order(10)
@Component
@Slf4j
public class MessageConsumer implements ApplicationRunner {

    @Autowired
    private WeChatervice weChatervice;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Thread msgHandle = new Thread(() -> {
            while (true) {
                if (SocketProperties.messages.size() > 0) {
                    try {
                        BaseRes res = SocketProperties.messages.take();
                        // log.info(res.toString());
                        if (Objects.equals(String.valueOf(InstructionUtil.currentUser().getWechatId()), res.getWechatId())) {
                            weChatervice.receiveMsg(res);
                        } else {
                            log.info("非当前微信消息");
                        }

                    } catch (Exception e) {
                        log.error("", e);
                        log.error("读取消息出错");
                    }
                } else {
                    SocketProperties.sleep(50);
                }
            }
        });
        msgHandle.setName("message-handle");
        msgHandle.setDaemon(true);
        msgHandle.start();
    }
}
