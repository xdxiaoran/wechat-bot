package com.harry.wechat.service;

import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.server.BaseRes;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 01:05
 * Desc: WeChatervice
 */
public interface WeChatervice {
    void receiveMsg(BaseRes baseRes);
}
