package com.harry.wechat.service;

import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.vo.GetAccountDto;
import com.harry.wechat.entity.Account;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 19:35
 * Desc: AccountService
 */
public interface AccountService {
    BaseResponse getAccounts(GetAccountDto param);

    BaseResponse save(Account dto);

    BaseResponse getAccountsRent();

    BaseResponse turnover(String start, String end);
}
