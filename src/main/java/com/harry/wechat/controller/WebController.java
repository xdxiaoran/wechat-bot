package com.harry.wechat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Harry
 * @date 2020/10/15
 * Time: 02:20
 * Desc: WebController
 */
@Controller
@CrossOrigin("*")
public class WebController {
    @RequestMapping("admin")
    public String login() {
        return "index";
    }
}
