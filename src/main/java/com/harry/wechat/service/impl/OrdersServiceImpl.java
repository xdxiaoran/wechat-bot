package com.harry.wechat.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.harry.wechat.config.FunType;
import com.harry.wechat.dao.AccountDao;
import com.harry.wechat.dao.OrdersDao;
import com.harry.wechat.dao.UserInfoDao;
import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.server.Instruction;
import com.harry.wechat.dto.vo.RentCancelDto;
import com.harry.wechat.dto.vo.RentDto;
import com.harry.wechat.entity.Account;
import com.harry.wechat.entity.Orders;
import com.harry.wechat.entity.UserInfo;
import com.harry.wechat.service.OrdersService;
import com.harry.wechat.util.InstructionUtil;
import com.harry.wechat.util.SocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.harry.wechat.util.DateUtils.getTimeDifference;

/**
 * @author Harry
 * @date 2020/10/12
 * Time: 15:38
 * Desc: OrdersServiceImpl
 */
@Slf4j
@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersDao ordersDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserInfoDao userInfoDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseResponse rent(RentDto dto) {

        Optional<UserInfo> userInfoOptional = userInfoDao.findByWxid(dto.getWxid());

        if (!userInfoOptional.isPresent()) {
            // todo
            // do something 通知管理员或刷新联系人列表
            return BaseResponse.fail("当前联系人列表已过期,请联系管理员");
        }

        UserInfo userInfo = userInfoOptional.get();
        if (userInfo.getStatus() != 0) {
            return BaseResponse.fail("");
        }
        if (!Objects.equals(userInfo.getStatus(), 0)) {
            return BaseResponse.fail("您的账号状态异常,请联系管理员");
        }
        if (dto.getType() == 1) {

            Optional<Account> accountOptional = accountDao.findById(dto.getAccountId());
            if (!accountOptional.isPresent()) {
                return BaseResponse.fail("所选账号不存在,请输入正确的序号");
            }

            Account account = accountOptional.get();
            if (account.getStatus() == 1) {
                // 租赁中
                return BaseResponse.fail("所选账号已被租出,请选择其它账号");
            } else if (account.getStatus() == 2) {
                // 其它不可用状态
                return BaseResponse.fail("所选账号状态异常,请联系管理员");
            }
            List<Orders> ordersList = ordersDao.findByAccountIdAndStatusNot(dto.getAccountId(), "0");
            if (CollectionUtils.isNotEmpty(ordersList)) {
                return BaseResponse.fail("所选账号已被租出,请选择其它账号");
            }


            // 开始下单。。。

            account.setStatus(1);

            Orders orders = Orders.builder()
                    .userId(userInfo.getId())
                    .accountId(account.getId())
                    .startTime(new Date())
                    .status("1")
                    .remindStatus(false)
                    .build();
            ordersDao.save(orders);
            accountDao.save(account);

            return BaseResponse.OK(account.getUsername() + "---" + account.getPassword());

        } else if (dto.getType() == 2) {
            // 下号
            List<Orders> orders = ordersDao.findByUserIdAndStatus(userInfo.getId(), "1");
            if (CollectionUtils.isEmpty(orders)) {
                return BaseResponse.fail("当前没有账号在租赁中");
            }
            if (orders.size() == 1) {
                Orders order = orders.get(0);
                Account account = accountDao.findById(order.getAccountId()).get();
                account.setStatus(0);
                accountDao.save(account);

                Date date = new Date();
                order.setEndTime(date);
                int cost = getTimeDifference(date, order.getStartTime());
                order.setCostTime(cost);
                double price = 1.0;
                if (StringUtils.isNotBlank(account.getPrice())) {
                    price = Double.parseDouble(account.getPrice());
                }

                BigDecimal amount = getAmount(cost, price);

                order.setAmount(amount);
                userInfo.setBalance(userInfo.getBalance().subtract(amount));

                order.setStatus("0");
                order.setPayStatus("0");

                accountDao.save(account);
                ordersDao.save(order);

                StringBuilder builder = new StringBuilder("结账成功\n");
                builder.append("本次消费时长: " + cost + " 分钟\n");
                builder.append("本次消费金额: " + amount + " 元\n");
                builder.append("账户余额: " + userInfo.getBalance());

                if (userInfo.getBalance().compareTo(BigDecimal.ZERO) == -1) {
                    // 通知
                    // 余额不足
                    builder.append("\n==================\n您的账户余额不足请及时充值");
                }
                return BaseResponse.OK(builder.toString());
            } else {
                // 多个账号，只退一个
                // 默认id
                // 备选账号id
                Account account = (dto.getAccountId() != null ? accountDao.getOne(dto.getAccountId()) : accountDao.findByUsername(dto.getAccountInfo()));

                if (account == null) {
                    return BaseResponse.fail("请输入正确的账号信息");
                }
                Optional<Orders> orderList = orders.stream().filter(order -> order.getAccountId().equals(account.getId())).findAny();
                if (!orderList.isPresent()) {
                    return BaseResponse.fail("请输入正确的账号信息");
                }
                Orders order = orderList.get();

                account.setStatus(0);
                // accountDao.save(account);

                Date date = new Date();
                order.setEndTime(date);
                int cost = getTimeDifference(date, order.getStartTime());
                order.setCostTime(cost);
                double price = 1.0;
                if (StringUtils.isNotBlank(account.getPrice())) {
                    price = Double.parseDouble(account.getPrice());
                }

                BigDecimal amount = getAmount(cost, price);

                order.setAmount(amount);
                userInfo.setBalance(userInfo.getBalance().subtract(amount));

                order.setStatus("0");
                order.setPayStatus("0");

                accountDao.save(account);
                ordersDao.save(order);

                StringBuilder builder = new StringBuilder("结账成功\n");
                builder.append("本次消费时长: " + cost + " 分钟\n");
                builder.append("本次消费金额: " + amount + " 元\n");
                builder.append("账户余额: " + userInfo.getBalance());

                if (userInfo.getBalance().compareTo(BigDecimal.ZERO) == -1) {
                    // 通知
                    // 余额不足
                    builder.append("\n==================\n您的账户余额不足请及时充值");
                }
                return BaseResponse.OK(builder.toString());
            }

        } else if (dto.getType() == 3) {
            // 全部下号
            List<Orders> orders = ordersDao.findByUserIdAndStatus(userInfo.getId(), "1");
            if (CollectionUtils.isEmpty(orders)) {
                // return BaseResponse.fail("当前没有账号在租赁中");
                return BaseResponse.OK("充值成功,账户余额: " + userInfo.getBalance());
            }
            if (orders.size() == 1) {
                Orders order = orders.get(0);
                Account account = accountDao.findById(order.getAccountId()).get();
                account.setStatus(0);

                Date date = new Date();
                log.info("date: " + date);
                order.setEndTime(date);
                int cost = getTimeDifference(date, order.getStartTime());
                log.info("cost: " + cost);
                order.setCostTime(cost);
                double price = 1.0;
                if (StringUtils.isNotBlank(account.getPrice())) {
                    price = Double.parseDouble(account.getPrice());
                }

                BigDecimal amount = getAmount(cost, price);
                log.info("amount: " + amount);

                order.setAmount(amount);
                userInfo.setBalance(userInfo.getBalance().subtract(amount));

                log.info("balance: " + userInfo.getBalance());

                order.setStatus("0");
                order.setPayStatus("0");

                accountDao.save(account);
                ordersDao.save(order);
                userInfoDao.save(userInfo);

                StringBuilder builder = new StringBuilder("结账成功\n");
                builder.append("本次消费时长: " + cost + " 分钟\n");
                builder.append("本次消费金额: " + amount + " 元\n");
                builder.append("账户余额: " + userInfo.getBalance());

                if (userInfo.getBalance().compareTo(BigDecimal.ZERO) == -1) {
                    // 通知
                    // 余额不足
                    builder.append("\n==================\n您的账户余额不足请及时充值");
                }
                return BaseResponse.OK(builder.toString());
            } else {
                // 多个账号，全部下号
                List<Account> accountList = new ArrayList<>();
                List<Orders> ordersList = new ArrayList<>();
                orders.forEach(order -> {
                    Account account = accountDao.findById(order.getAccountId()).get();
                    account.setStatus(0);
                    accountDao.save(account);

                    Date date = new Date();
                    order.setEndTime(date);
                    int cost = getTimeDifference(date, order.getStartTime());
                    order.setCostTime(cost);
                    double price = 1.0;
                    if (StringUtils.isNotBlank(account.getPrice())) {
                        price = Double.parseDouble(account.getPrice());
                    }

                    BigDecimal amount = getAmount(cost, price);

                    order.setAmount(amount);
                    userInfo.setBalance(userInfo.getBalance().subtract(amount));

                    order.setStatus("0");
                    order.setPayStatus("0");

                    accountList.add(account);
                    ordersList.add(order);
                });

                accountDao.saveAll(accountList);
                ordersDao.saveAll(ordersList);

                userInfoDao.save(userInfo);

                int costs = 0;
                BigDecimal prices = BigDecimal.ZERO;
                for (Orders order : orders) {
                    costs += order.getCostTime();
                    prices.add(order.getAmount());
                }

                StringBuilder builder = new StringBuilder("结账成功\n");
                builder.append("本次消费账号: " + orders.size() + " 个\n");
                builder.append("本次消费共计时长: " + costs + " 分钟\n");
                builder.append("本次消费共计金额: " + prices + " 元\n");
                builder.append("账户余额: " + userInfo.getBalance());
                if (userInfo.getBalance().compareTo(BigDecimal.ZERO) == -1) {
                    // 通知
                    // 余额不足
                    builder.append("\n==================\n您的账户余额不足请及时充值");
                }
                return BaseResponse.OK(builder.toString());
            }

        }

        return BaseResponse.fail("操作类型错误");
    }

    public BigDecimal getAmount(int cost, double price) {
        int h = cost / 60;
        int m = cost % 60;

        if (m < 20) {
            return BigDecimal.valueOf(h * price);
        } else if (m > 50) {
            return BigDecimal.valueOf((h + 1) * price);
        } else {
            return BigDecimal.valueOf((h + 0.5) * price);
        }
    }


    @Override
    public BaseResponse getOrders(PageRequest pageRequest) {
        return BaseResponse.OK(ordersDao.findAll(pageRequest));
    }

    @Override
    public BaseResponse cancelRent(RentCancelDto dto) {

        if (dto.getAccountIds().size() == 1) {
            // 单个账号
            // 充值后再退号
            Account account = accountDao.getOne(dto.getAccountIds().get(0));
            List<Orders> orders = ordersDao.findByAccountIdAndStatusNot(account.getId(), "0");
            if (CollectionUtils.isEmpty(orders)) {
                if (account.getStatus() != 0){
                    account.setStatus(0);
                }
            } else {
                Orders order = orders.get(0);
                account.setStatus(0);
                Date date = new Date();
                order.setEndTime(date);
                int cost = getTimeDifference(date, order.getStartTime());
                order.setCostTime(cost);
                double price = 1.0;
                if (StringUtils.isNotBlank(account.getPrice())) {
                    price = Double.parseDouble(account.getPrice());
                }

                BigDecimal amount = getAmount(cost, price);

                order.setAmount(amount);

                UserInfo userInfo = userInfoDao.getOne(order.getUserId());

                userInfo.setBalance(userInfo.getBalance().subtract(amount));
                order.setStatus("0");
                order.setPayStatus("0");
                accountDao.save(account);
                ordersDao.save(order);

                StringBuilder builder = new StringBuilder("下号成功\n");
                builder.append("本次消费时长: " + cost + " 分钟\n");
                builder.append("本次消费金额: " + amount + " 元\n");
                builder.append("账户余额: " + userInfo.getBalance());

                InstructionUtil.sendText(userInfo.getWxid(),builder.toString());

                userInfoDao.save(userInfo);
                ordersDao.save(order);

            }
            accountDao.save(account);
        }else {
            // 批量下号，全部下号
            List<Orders> orders = ordersDao.findByAccountIdInAndStatusNot(dto.getAccountIds(),"0");
            List<Account> accounts =  accountDao.findByIdIn(dto.getAccountIds());
            if (CollectionUtils.isNotEmpty(orders)){
                List<UserInfo> users = userInfoDao.findAll();
                Map<Long, UserInfo> userMap = Maps.uniqueIndex(users, UserInfo::getId);
                Map<Long, Account> accountMap = Maps.uniqueIndex(accounts, Account::getId);
                Date date = new Date();
                List<Account> accountUpdate = new ArrayList<>();
                List<UserInfo> userUpdate = new ArrayList<>();
                List<Orders> orderUpdate = new ArrayList<>();
                List<Instruction> instructions = Lists.newArrayList();
                orders.forEach( order -> {
                    Account account = accountMap.get(order.getAccountId());
                    UserInfo userInfo = userMap.get(order.getUserId());
                    account.setStatus(0);
                    order.setEndTime(date);
                    int cost = getTimeDifference(date, order.getStartTime());
                    order.setCostTime(cost);
                    double price = 1.0;
                    if (StringUtils.isNotBlank(account.getPrice())) {
                        price = Double.parseDouble(account.getPrice());
                    }

                    BigDecimal amount = getAmount(cost, price);

                    order.setAmount(amount);
                    userInfo.setBalance(userInfo.getBalance().subtract(amount));
                    order.setStatus("0");
                    order.setPayStatus("1");
                    accountUpdate.add(account);
                    userUpdate.add(userInfo);
                    orderUpdate.add(order);

                    StringBuilder builder = new StringBuilder("下号成功\n");
                    builder.append("本次消费时长: " + cost + " 分钟\n");
                    builder.append("本次消费金额: " + amount + " 元\n");
                    builder.append("账户余额: " + userInfo.getBalance());

                    // InstructionUtil.sendText(userInfo.getWxid(),builder.toString());
                    instructions.add(Instruction.of(userInfo.getWxid(), builder.toString(), FunType.SENDTEXT.getFunid()));
                });

                accountDao.saveAll(accountUpdate);
                userInfoDao.saveAll(userUpdate);
                ordersDao.saveAll(orderUpdate);

                new Thread(() -> {
                    Iterator<Instruction> iterator = instructions.iterator();
                    while (iterator.hasNext()){
                        InstructionUtil.postForObject(iterator.next(),Object.class);
                        SocketProperties.sleep(1000);
                    }
                }).start();
            }
        }
        return BaseResponse.OK;
    }
}
