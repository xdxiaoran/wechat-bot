package com.harry.wechat.controller;

import com.harry.wechat.config.FunType;
import com.harry.wechat.dao.ConfigDao;
import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.RentModeDto;
import com.harry.wechat.dto.server.Instruction;
import com.harry.wechat.dto.vo.*;
import com.harry.wechat.entity.Account;
import com.harry.wechat.entity.Config;
import com.harry.wechat.entity.UserInfo;
import com.harry.wechat.service.AccountService;
import com.harry.wechat.service.OrdersService;
import com.harry.wechat.service.UserInfoService;
import com.harry.wechat.util.InstructionUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.harry.wechat.util.Constance.isCardMode;
import static com.harry.wechat.util.Constance.silentMode;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 10:53
 * Desc: BotController
 */
@RestController
@CrossOrigin("*")
@Slf4j
public class BotController {


    @GetMapping("/client/new")
    public BaseResponse openClient() {

        InstructionUtil.sendRequestWithOutId(Instruction.of(FunType.INIT.getFunid()));

        return BaseResponse.OK;
    }

    @Autowired
    private AccountService accountService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private ConfigDao configDao;


    @PostMapping("accounts")
    public BaseResponse getAccount(@RequestBody @ApiParam(value = "dto") GetAccountDto dto) {

        return accountService.getAccounts(dto);
    }

    @PostMapping(value = "user_list")
    public BaseResponse getUserList(@RequestBody @ApiParam(value = "dto") GetUserDto dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize());
        return userInfoService.getUserList(dto, pageRequest);
    }

    @PostMapping(value = "rent")
    public BaseResponse rent(@RequestBody @ApiParam(value = "dto") RentDto dto) {
        return ordersService.rent(dto);
    }

    @PostMapping("/accounts/rent")
    public BaseResponse getAccountRent() {

        return accountService.getAccountsRent();
    }

    @GetMapping(value = "orders")
    public BaseResponse getOrders(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        return ordersService.getOrders(pageRequest);
    }

    @PostMapping(value = "recharge")
    @ApiOperation(value = "充值", notes = "后台操作 type 选择 1")
    public BaseResponse recharge(@RequestBody @ApiParam(value = "dto") RechargeDto dto) {
        return userInfoService.recharge(dto, true) ? BaseResponse.OK : BaseResponse.fail("充值失败");
    }

    @PostMapping(value = "/rent/cancel")
    public BaseResponse cancelRent(@RequestBody @ApiParam(value = "dto") RentCancelDto dto) {
        return ordersService.cancelRent(dto);
    }

    @PostMapping("/account/save")
    public BaseResponse saveAccount(@RequestBody @ApiParam(value = "dto") Account dto) {
        return accountService.save(dto);
    }

    @PostMapping("/user/save")
    public BaseResponse saveUser(@RequestBody @ApiParam(value = "dto") UserInfo dto) {
        return userInfoService.save(dto);
    }

    @GetMapping("/mode")
    public BaseResponse mode(@RequestParam(value = "status") String status) {
        if (Objects.equals("1", status)) {
            log.info("开启静默模式");
            silentMode = true;
        } else {
            log.info("关闭静默模式");
            silentMode = false;
        }
        return BaseResponse.OK;
    }

    @GetMapping("/getmode")
    public BaseResponse getmode() {
        return BaseResponse.OK(silentMode ? "1" : 0);
    }

    @GetMapping("/card/mode")
    public BaseResponse cardMode(@RequestParam(value = "status") String status) {
        if (Objects.equals("1", status)) {
            log.info("开启名片模式");
            isCardMode = true;
        } else {
            log.info("关闭名片模式");
            isCardMode = false;
        }
        return BaseResponse.OK;
    }

    @GetMapping("/card/getmode")
    public BaseResponse cardGetmode() {
        return BaseResponse.OK(isCardMode ? "1" : 0);
    }


    @GetMapping("/recharge/list")
    public BaseResponse rechargeList(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        return userInfoService.rechargeList(pageRequest);
    }

    @GetMapping("/rentgroup/mark")
    @ApiOperation(value = "租号群标记", notes = "标记为可租号群, 1标记为可租号群，0 标记为不可租号群")
    public BaseResponse markAsRentGroup(@RequestParam(value = "userId") Long userId, @RequestParam(value = "status") Integer status) {
        return userInfoService.markAsRentGroup(userId, status);
    }

    @GetMapping("/turnover")
    public BaseResponse turnover(@RequestParam(value = "start") String start,
                                 @RequestParam(value = "end") String end) {
        return accountService.turnover(start, end);
    }

    @GetMapping("/amount/total")
    public BaseResponse totalAmount() {
        return ordersService.totalAmount();
    }

    @PostMapping("/rentMode")
    public BaseResponse rentMode(@RequestBody @ApiParam(value = "dto") RentModeDto dto) {
        return userInfoService.rentMode(dto);
    }

    @GetMapping("/config/list")
    public BaseResponse listConfig(@RequestParam(value = "lab", required = false) String lab,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        Page<Config> configs;
        if (StringUtils.isNotBlank(lab)) {
            configs = configDao.findByLabLike(lab, pageRequest);
        } else {
            configs = configDao.findAll(pageRequest);
        }
        return BaseResponse.OK(configs);

    }

    @PostMapping("/config/save")
    @Transactional
    public BaseResponse saveConfig(@RequestBody @ApiParam(value = "dto") ConfigDto dto) {

        Config config;
        if (dto.getId() == null || dto.getId() == 0) {
            config = new Config();
        } else {
            config = configDao.getOne(dto.getId());
        }
        BeanUtils.copyProperties(dto, config);

        configDao.save(config);

        return BaseResponse.OK;

    }

    @GetMapping("/config/delete")
    @Transactional
    public BaseResponse delConfig(@RequestParam(value = "id") Long id) {
        if (id != null) {
            configDao.deleteById(id);
        }
        return BaseResponse.OK;
    }

}
