package com.harry.wechat.util;

import com.harry.wechat.config.FunType;
import com.harry.wechat.dto.server.Instruction;
import com.harry.wechat.dto.server.LoginDto;
import com.harry.wechat.dto.server.LoginUser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Harry
 * @date 2020/10/22
 * Time: 23:58
 * Desc: InstructionUtil
 */
public class InstructionUtil {

    public static LoginUser loginUser = null;

    private static HttpUtil httpUtil;

    public static void setHttpUtil(HttpUtil httpUtil) {
        InstructionUtil.httpUtil = httpUtil;
    }

    public static <T> List<T> postForArray(Instruction instruction, Class<T> tClass) {
        instruction.setWeChatID(currentUser().getWechatId());

        List<T> res = httpUtil.postForArray(instruction, tClass);
        for (int i = 0; i < 3; i++) {
            if (res == null){
                res = httpUtil.postForArray(instruction, tClass);
                if (res != null){
                    return res;
                }
            }else {
                return res;
            }
        }
        return null;
    }

    public static <T> T postForObject(Instruction instruction, Class<T> tClass) {
        instruction.setWeChatID(currentUser().getWechatId());
        T res = httpUtil.postForObject(instruction, tClass);
        for (int i = 0; i < 3; i++) {
            if (res == null){
                res = httpUtil.postForObject(instruction, tClass);
                if (res != null){
                    return res;
                }
            }else {
                return res;
            }
        }
        return null;
    }

    public static void sendText(String wxid,String context){
        postForObject(Instruction.of(wxid, context,FunType.SENDTEXT.getFunid()),Object.class);
    }


    public static LoginUser currentUser() {
        if (loginUser == null || loginUser.getWechatId() != 0) {
            List<LoginDto> users = httpUtil.postForArray(Instruction.builder().funid(FunType.LOGIN.getFunid()).build(), LoginDto.class);
            if (CollectionUtils.isEmpty(users)){
                httpUtil.postForObject(Instruction.builder().funid(FunType.INIT.getFunid()).build(),Map.class);
            }else {
                if (StringUtils.isNotBlank(users.get(0).getData().getWxid())){
                    loginUser = LoginUser.of(users.get(0).getData(), users.get(0).getWeChatID());
                }
            }
        }
        return loginUser;
    }

    public static Map sendRequestWithOutId(Instruction instruction) {
        return httpUtil.postForObject(instruction, Map.class);
    }

    public static void weakUp(){
        List<LoginDto> users = httpUtil.postForArray(Instruction.builder().funid(FunType.LOGIN.getFunid()).build(), LoginDto.class);
        if (CollectionUtils.isEmpty(users)){
            httpUtil.postForObject(Instruction.builder().funid(FunType.INIT.getFunid()).build(),Map.class);
        }
    }


}
