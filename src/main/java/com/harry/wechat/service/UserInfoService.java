package com.harry.wechat.service;

import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.server.FriendDto;
import com.harry.wechat.dto.vo.GetUserDto;
import com.harry.wechat.dto.vo.RechargeDto;
import com.harry.wechat.entity.UserInfo;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * @author Harry
 * @date 2020/10/9
 * Time: 10:10
 * Desc: UserInfoService
 */
public interface UserInfoService {
    void syncUserInfo(List<FriendDto> accounts);

    BaseResponse getUserList(GetUserDto dto, PageRequest pageRequest);

    UserInfo getUserByWxid(String wxid);

    Boolean recharge(RechargeDto dto);

    Boolean recharge(RechargeDto dto,boolean notify);

    BaseResponse save(UserInfo dto);

    BaseResponse rechargeList(PageRequest pageRequest);
}
