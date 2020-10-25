package com.harry.wechat.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 00:01
 * Desc: HttpUtilInit
 */
@Configuration
public class HttpUtilInit {

    @Autowired
    private HttpUtil httpUtil;


    @PostConstruct
    public void init(){
        InstructionUtil.setHttpUtil(httpUtil);
    }
}
