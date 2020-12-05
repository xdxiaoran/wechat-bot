package com.harry.wechat.init;

import com.harry.wechat.config.FunType;
import com.harry.wechat.dto.server.FriendRes;
import com.harry.wechat.dto.server.Instruction;
import com.harry.wechat.dto.server.LoginUser;
import com.harry.wechat.service.UserInfoService;
import com.harry.wechat.util.InstructionUtil;
import com.harry.wechat.util.SocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 01:00
 * Desc: WeakUpWechat
 */
@SuppressWarnings("ALL")
@Order(3)
@Component
@Slf4j
public class WeakUpWechat implements ApplicationRunner {


    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO: 2020/10/23  唤起微信登录（启动hook？）
        log.info("启动微信");

        /**
         * 后台检测微信
         * 同步联系人到数据库
         */
        Thread infoHandle = new Thread(() -> {
            // boolean flag = true;
            while (true) {
                try {

                    InstructionUtil.weakUp();
                    LoginUser loginUser = InstructionUtil.currentUser();
                    if (loginUser != null && StringUtils.isNotBlank(loginUser.getWxid())) {
                        // flag = !flag;
                        FriendRes friendRes = InstructionUtil.postForObject(Instruction.builder().funid(FunType.FRIENDLIST.getFunid()).build(), FriendRes.class);
                        if (friendRes != null && CollectionUtils.isNotEmpty(friendRes.getFdlist())) {
                            userInfoService.syncUserInfo(friendRes.getFdlist());
                        }
                        SocketProperties.sleep(60_000);
                    } else {
                        SocketProperties.sleep(60_000);
                    }
                }catch (Exception e){
                    log.info("联系人同步失败");
                }
            }
        });
        infoHandle.setName("info-handle");
        infoHandle.setDaemon(true);
        infoHandle.start();
    }
}
