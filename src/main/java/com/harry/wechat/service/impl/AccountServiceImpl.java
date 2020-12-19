package com.harry.wechat.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.harry.wechat.config.FunType;
import com.harry.wechat.dao.AccountDao;
import com.harry.wechat.dao.OrdersDao;
import com.harry.wechat.dao.SQLSupporter;
import com.harry.wechat.dao.UserInfoDao;
import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.server.FriendRes;
import com.harry.wechat.dto.server.Instruction;
import com.harry.wechat.dto.server.LoginUser;
import com.harry.wechat.dto.vo.AccountDto;
import com.harry.wechat.dto.vo.GetAccountDto;
import com.harry.wechat.entity.Account;
import com.harry.wechat.entity.Orders;
import com.harry.wechat.entity.UserInfo;
import com.harry.wechat.service.AccountService;
import com.harry.wechat.service.UserInfoService;
import com.harry.wechat.util.InstructionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.harry.wechat.util.DateUtils.getTimeDifference;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 19:35
 * Desc: AccountServiceImpl
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private SQLSupporter supporter;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private OrdersDao ordersDao;
    @Autowired
    private UserInfoService userInfoService;

    @Override
    public BaseResponse getAccounts(GetAccountDto param) {

        if (StringUtils.isNotBlank(param.getAccountName())) {
            if (param.getAccountName().matches("^[-\\+]?[\\d]*$") && param.getAccountName().length() < 8) {

                return BaseResponse.OK(accountDao.findByIdAndStatus(Long.parseLong(param.getAccountName()), 0));
            } else {
                return BaseResponse.OK(accountDao.findByUsername(param.getAccountName()));
            }
        }

        StringBuilder sql = new StringBuilder("select * from account where 1 = 1 ");

        int level = 0;
        if (Objects.equals("root", param.getWxid())) {
            level = 3;
        } else {
            Optional<UserInfo> userInfoOptional = userInfoDao.findByWxid(param.getWxid());
            if (!userInfoOptional.isPresent()) {
                // todo
                // do something 通知管理员或刷新联系人列表
                syncFriend();
                userInfoOptional = userInfoDao.findByWxid(param.getWxid());
                // return BaseResponse.fail("当前联系人列表已过期,请联系管理员");
            }

            UserInfo userInfo = userInfoOptional.get();
            if (userInfo.getStatus() != 0) {
                return BaseResponse.fail("");
            }
            level = userInfo.getType();

            sql.append(" and status = 0");
        }

        sql.append(" and vip_level <= " + level);

        if (CollectionUtils.isNotEmpty(param.getServers())) {
            sql.append(" and server in ('" + String.join(",", param.getServers()) + "')");
        }

        if (CollectionUtils.isNotEmpty(param.getHeros())) {
            String collect = param.getHeros().stream().map(hero -> " FIND_IN_SET('" + hero + "',hero_list)").collect(Collectors.joining(" or "));
            sql.append(" and ( " + collect + ") ");
        }

        if (CollectionUtils.isNotEmpty(param.getModes()) && CollectionUtils.isNotEmpty(param.getLevelIndex())) {
            List<String> indexs = Lists.newArrayList();
            String levels = param.getLevelIndex().stream().map(String::valueOf).collect(Collectors.joining(","));
            // String levels = String.join(",", param.getLevelIndex());
            param.getModes().forEach(mode -> {
                if (mode.equals("单双排位")) {
                    indexs.add(" rank_index_single in (" + levels + ")");
                }
                if (mode.equals("灵活排位")) {
                    // rank_index_flexible
                    indexs.add(" rank_index_flexible in (" + levels + ")");
                }
                if (mode.equals("云顶之弈")) {
                    //    chess_index
                    indexs.add(" chess_index in (" + levels + ")");
                }
            });
            sql.append(" and (" + String.join(" or ", indexs) + ")");

        }

        log.info("sql : " + sql.toString());
        List<Account> accounts = supporter.queryBySql(sql.toString(), Account.class);

        return BaseResponse.OK(accounts);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse save(Account dto) {

        Account accountOld;
        if (dto.getId() == null || dto.getId() == 0) {
            accountOld = new Account();
        } else {
            accountOld = accountDao.getOne(dto.getId());
        }

        BeanUtils.copyProperties(dto, accountOld);

        accountDao.save(accountOld);

        return BaseResponse.OK;
    }

    protected void syncFriend() {
        LoginUser loginUser = InstructionUtil.currentUser();
        if (loginUser != null && StringUtils.isNotBlank(loginUser.getWxid())) {
            // flag = !flag;
            FriendRes friendRes = InstructionUtil.postForObject(Instruction.builder().funid(FunType.FRIENDLIST.getFunid()).build(), FriendRes.class);
            if (friendRes != null && CollectionUtils.isNotEmpty(friendRes.getFdlist())) {
                userInfoService.syncUserInfo(friendRes.getFdlist());
            }
        }
    }

    @Override
    public BaseResponse getAccountsRent() {

        List<Account> accounts = accountDao.findByStatus(1);

        List<Orders> ordersList = ordersDao.findByAccountIdInAndStatus(accounts.stream().map(Account::getId).collect(Collectors.toList()), "1");

        List<UserInfo> userInfos = userInfoDao.findByIdIn(ordersList.stream().map(Orders::getUserId).collect(Collectors.toList()));

        Map<Long, UserInfo> userInfoMap = Maps.uniqueIndex(userInfos, UserInfo::getId);

        Map<Long, Orders> ordersMap = Maps.uniqueIndex(ordersList, Orders::getAccountId);

        List<AccountDto> accountDtos = Lists.newArrayList();

        Date date = new Date();
        accounts.forEach(account -> {
            AccountDto accountDto = new AccountDto();
            BeanUtils.copyProperties(account, accountDto);
            Orders order = ordersMap.get(account.getId());
            if (order != null) {
                int cost = getTimeDifference(date, order.getStartTime());
                accountDto.setCostTime(cost);
                UserInfo userInfo = userInfoMap.get(order.getUserId());
                if (userInfo != null) {
                    accountDto.setWxName(userInfo.getNickName());
                }
            }
            accountDtos.add(accountDto);
        });

        return BaseResponse.OK(accountDtos);
    }

    @Override
    public BaseResponse turnover(String start, String end) {
        List<Map<String, Object>> result = accountDao.findBySql(start, end);
        return BaseResponse.OK(result);
    }
}
