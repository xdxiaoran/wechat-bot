package com.harry.wechat.service.impl;

import com.harry.wechat.config.CardConfig;
import com.harry.wechat.config.FunType;
import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.server.BaseRes;
import com.harry.wechat.dto.server.Instruction;
import com.harry.wechat.dto.vo.GetAccountDto;
import com.harry.wechat.dto.vo.RechargeDto;
import com.harry.wechat.dto.vo.RentDto;
import com.harry.wechat.entity.Account;
import com.harry.wechat.entity.UserInfo;
import com.harry.wechat.service.AccountService;
import com.harry.wechat.service.OrdersService;
import com.harry.wechat.service.UserInfoService;
import com.harry.wechat.service.WeChatervice;
import com.harry.wechat.util.InstructionUtil;
import com.harry.wechat.util.WordUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

import static com.harry.wechat.util.Constance.*;
import static com.harry.wechat.util.WordUtil.transferAmount;
import static com.harry.wechat.util.WordUtil.transferId;

/**
 * @author Harry
 * @date 2020/10/23
 * Time: 01:05
 * Desc: WeChaterviceImpl
 */
@Service
@Slf4j
public class WeChaterviceImpl implements WeChatervice {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserInfoService userInfoService;

    @Value("${card.mode:false}")
    private Boolean isCardMode;

    @Autowired
    private CardConfig cardConfig;

    @Override
    public void receiveMsg(BaseRes baseRes) {

        UserInfo userInfo = userInfoService.getUserByWxid(baseRes.getWxid());

        // 屏蔽一般群消息
        if (baseRes.getWxid().endsWith("@chatroom") && !userInfo.getIsRentGroup()) {
            return;
        }
        // 偷登号检测
        // 关键词检测

        switch (baseRes.msgType()) {
            case TEXT:
                // log.info(baseRes.getContent());

                if (BLACK_NAME.stream().anyMatch(userInfo.getRemarkName()::contains)) {
                    return;
                }


                if (silentMode) {
                    return;
                }

                if (isCardMode) {
                    if (USER_STATUS_WORD.containsKey(baseRes.getWxid())) {
                        return;
                    } else {

                        Instruction instruction = Instruction.builder()
                                .funid(FunType.CARD.getFunid())
                                .wxid(baseRes.getWxid())
                                .content(String.format(cardConfig.getContent(), cardConfig.getBigheadimgurl(), cardConfig.getSmallheadimgurl(), cardConfig.getUsername(), cardConfig.getNickname()))
                                .build();


                        InstructionUtil.sendText(baseRes.getWxid(), cardConfig.getAutoword());
                        InstructionUtil.postForObject(instruction, Object.class);
                        USER_STATUS_WORD.put(baseRes.getWxid(), "send");
                        return;
                    }
                }

                String result = action(baseRes.getWxid(), baseRes.getContent());

                if (StringUtils.isBlank(result)) {
                    return;
                } else if (!Objects.equals("0", result)) {
                    // send
                    InstructionUtil.sendText(baseRes.getWxid(), result);
                    if (result.contains("下单成功")) {
                        InstructionUtil.sendText(baseRes.getWxid(), "请不要提前转账，打完之后转账会自动下号");
                    }
                    return;
                }

                // words
                String res = WordUtil.collect(baseRes.getWxid(), baseRes.getContent(), accountService, userInfo.getType() == 2);

                if (StringUtils.isNotBlank(res)) {
                    InstructionUtil.sendText(baseRes.getWxid(), res);
                }

                break;
            case TRANSFER:

                log.info("疑似转账消息");
                String amount = transferAmount(baseRes.getContent());
                String transferId = transferId(baseRes.getContent());
                if (StringUtils.isBlank(amount) || StringUtils.isBlank(transferId)
                        || Objects.equals(amount, "0") || Objects.equals(amount, "0")) {
                    // 非转账消息
                    return;
                }
                if (baseRes.getAttach2() == 1) {
                    // 收款确认
                    log.info("确认收款");
                    // UserInfo userInfo = userInfoService.getUserByWxid(baseRes.getWxid());

                    log.info("转账金额: {} 元", amount);
                    log.info("转账ID: " + transferId);

                    if (!Objects.equals("0", amount)) {
                        RechargeDto dto = RechargeDto.builder()
                                .userId(userInfo.getId()).balance(BigDecimal.valueOf(Double.parseDouble(amount)))
                                .type(0).build();

                        boolean restult = userInfoService.recharge(dto);

                        if (isCardMode || silentMode) {
                            return;
                        }
                        if (restult) {
                            // 充值成功
                            if (userInfo.getRentMode() == 1) {
                                // 先打后租
                                // 开始退号
                                RentDto rentDto = RentDto.builder()
                                        .wxid(baseRes.getWxid())
                                        .type(3)
                                        .build();

                                InstructionUtil.sendText(baseRes.getWxid(), rent(rentDto));

                            } else {
                                // TODO: 2020/10/14 先付款，后下单
                            }
                        }
                    }
                } else {

                    // 尝试收款
                    log.info("收到转账");
                    // 转账
                    Instruction instruction = Instruction.builder()
                            .wxid(baseRes.getWxid())
                            .funid(FunType.TRANSFER.getFunid())
                            .transferid(transferId)
                            .build();

                    String resp = InstructionUtil.postForObject(instruction, String.class);
                    log.info("收款结果: " + resp);
                }


                break;
            default:
                log.info("其它消息");

        }
    }


    /**
     * @param msg
     * @return "0" 进行下一步
     * "" 不做任何处理，流程终止
     * 字符串，发送到微信
     */
    public String action(String wxid, String msg) {


        if (msg.matches("^[-\\+]?[\\d]*$")) {

            if (Long.parseLong(msg) < 100) {
                // 特殊编号
                // 暂不做处理
                return "";
            }

            if (CollectionUtils.isEmpty(USER_STATUS.get(wxid))) {
                // 半小时内为触发选号界面
                // 不做任何处理
                return "";
            }

            // 选择账号
            RentDto dto = RentDto.builder()
                    .accountId(Long.parseLong(msg))
                    .wxid(wxid)
                    .type(1)
                    .build();
            BaseResponse response = ordersService.rent(dto);

            if (Objects.equals(response.getCode(), 200)) {
                return "下单成功\n" + response.getData();
            } else {
                return "下单失败\n" + response.getMessage();
            }
        } else if (Objects.equals("全部下号", msg)) {
            RentDto dto = RentDto.builder()
                    .wxid(wxid)
                    .type(3)
                    .build();
            return rent(dto);

        } else if (END_KEYWORD.stream().anyMatch(msg::contains)) {
            // 下号
            if (END_KEYWORD.stream().anyMatch(msg::equals)) {
                RentDto dto = RentDto.builder()
                        .wxid(wxid)
                        .type(2)
                        .build();
                return rent(dto);
            } else {
                if (msg.contains("----")) {
                    RentDto dto = RentDto.builder()
                            .wxid(wxid)
                            .type(2)
                            .accountInfo(msg.substring(0, msg.indexOf("----")))
                            .build();
                    return rent(dto);
                } else if (msg.contains("---")) {
                    RentDto dto = RentDto.builder()
                            .wxid(wxid)
                            .type(2)
                            .accountInfo(msg.substring(0, msg.indexOf("---")))
                            .build();
                    return rent(dto);
                } else if (msg.matches("[0-9]+.*")) {

                    int index = 0;
                    for (int i = 0; i < msg.length(); i++) {
                        if (!Character.isDigit(msg.charAt(i))) {
                            index = i;
                            break;
                        }
                    }
                    RentDto dto = RentDto.builder()
                            .wxid(wxid)
                            .type(2)
                            .accountId(Long.parseLong(msg.substring(0, index)))
                            .build();
                    return rent(dto);
                }
            }
        } else if (QUERY_KEYWORD.stream().anyMatch(msg::contains)) {
            String accountName = null;
            if (msg.contains("----")) {
                accountName = msg.substring(0, msg.indexOf("----"));
            } else if (msg.contains("---")) {
                accountName = msg.substring(0, msg.indexOf("---"));
            } else if (msg.matches("[0-9]+.*")) {
                int index = 0;
                for (int i = 0; i < msg.length(); i++) {
                    if (!Character.isDigit(msg.charAt(i))) {
                        index = i;
                        break;
                    }
                }
                accountName = msg.substring(0, index);
            }

            if (StringUtils.isNotEmpty(accountName)) {
                return "查询格式有误\n 请输入: 用户名---密码 是否可用 查询账号信息";
            }

            GetAccountDto dto = GetAccountDto.builder()
                    .accountName(accountName)
                    .build();

            BaseResponse response = accountService.getAccounts(dto);

            if (response.getData() == null) {
                return "查询格式有误\n 请输入: 用户名---密码 是否可用 查询账号信息";
            }

            Account account = (Account) response.getData();
            if (account.getStatus() == 0) {
                // 可用
                return "当前账号可用,请输入 编号 " + account.getId() + " 下单使用";
            } else {
                // 不可用
                return "当前账号不可用,请输入条件查询可用账号";
            }


        } else if (msg.contains("比尔特沃夫") || msg.contains("皮儿吉沃特")) {
            return "需要电5皮尔特沃夫还是  网1比尔吉沃特";
        }

        return "0";
    }


    private String rent(RentDto dto) {
        BaseResponse response = ordersService.rent(dto);
        if (Objects.equals(response.getCode(), 200)) {
            return response.getData().toString();
        } else {
            return response.getMessage();
        }
    }

}
