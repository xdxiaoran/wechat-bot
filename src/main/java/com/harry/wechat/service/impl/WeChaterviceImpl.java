package com.harry.wechat.service.impl;

import com.google.common.collect.Maps;
import com.harry.wechat.config.CardConfig;
import com.harry.wechat.config.FunType;
import com.harry.wechat.dao.AccountDao;
import com.harry.wechat.dao.ConfigDao;
import com.harry.wechat.dao.OrdersDao;
import com.harry.wechat.dto.BaseResponse;
import com.harry.wechat.dto.server.BaseRes;
import com.harry.wechat.dto.server.FriendRes;
import com.harry.wechat.dto.server.Instruction;
import com.harry.wechat.dto.server.LoginUser;
import com.harry.wechat.dto.vo.GetAccountDto;
import com.harry.wechat.dto.vo.RechargeDto;
import com.harry.wechat.dto.vo.RentDto;
import com.harry.wechat.entity.Account;
import com.harry.wechat.entity.Config;
import com.harry.wechat.entity.Orders;
import com.harry.wechat.entity.UserInfo;
import com.harry.wechat.service.AccountService;
import com.harry.wechat.service.OrdersService;
import com.harry.wechat.service.UserInfoService;
import com.harry.wechat.service.WeChatervice;
import com.harry.wechat.util.InstructionUtil;
import com.harry.wechat.util.SocketProperties;
import com.harry.wechat.util.WordUtil;
import com.harry.wechat.util.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.harry.wechat.util.Constance.*;
import static com.harry.wechat.util.DateUtils.getTimeDifference;
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

    @Autowired
    private ConfigDao configDao;
    @Autowired
    private AccountDao accountDao;

    @Autowired
    private CardConfig cardConfig;
    @Autowired
    private OrdersDao ordersDao;

    @Override
    public void receiveMsg(BaseRes baseRes) {

        UserInfo userInfo = userInfoService.getUserByWxid(baseRes.getWxid());

        // 屏蔽一般群消息
        if (baseRes.getWxid().endsWith("@chatroom") && !userInfo.getIsRentGroup()) {
            return;
        }

        // demo
        // if (!userInfo.getRemarkName().equals("机器人测试")){
        //     return;
        // }
        // 偷登号检测
        // 关键词检测

        switch (baseRes.msgType()) {
            case TEXT:
                // log.info(baseRes.getContent());

                initConfig();

                if (StringUtils.isNotBlank(userInfo.getRemarkName()) && BLACK_NAME.stream().anyMatch(userInfo.getRemarkName()::contains)) {
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

                String result = action(baseRes.getWxid(), baseRes.getContent(),userInfo.getId());

                if (StringUtils.isBlank(result)) {
                    return;
                } else if (!Objects.equals("0", result)) {
                    // send
                    // InstructionUtil.sendText(baseRes.getWxid(), result.replace("下单成功", ""));

                    if (result.contains("下单成功")) {
                        InstructionUtil.sendText(baseRes.getWxid(), "请不要提前转账，打完之后转账会自动下号");
                    } else {
                        InstructionUtil.sendText(baseRes.getWxid(), result);
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

                                InstructionUtil.sendText(baseRes.getWxid(), "充值成功");
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

            case ADD_FRIEND:

                String attributes = XmlUtils.getAttributes(baseRes.getContent(), "encryptusername", "ticket");
                Instruction instruction = Instruction.of(FunType.AGREEFRIEND.getFunid());
                String[] split = attributes.split("--");
                instruction.setV1(split[0]);
                instruction.setV4(split[1]);

                InstructionUtil.postForObject(instruction, Object.class);

                // 更新好友列表
                SocketProperties.sleep(1);
                syncFriend();

                InstructionUtil.sendText(baseRes.getWxid(),"请输入大区、段位、英雄来搜索账号");
                break;
            default:
                log.info("其它消息");

        }
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

    private void initConfig() {
        List<Config> dicts = configDao.findAll();

        Map<String, List<Config>> dictMap = dicts.stream().collect(Collectors.groupingBy(Config::getKey));

        if (CollectionUtils.isNotEmpty(dictMap.get("1"))) {
            END_KEYWORD.addAll(dictMap.get("1").stream().map(Config::getValue).collect(Collectors.toSet()));
        }
        if (CollectionUtils.isNotEmpty(dictMap.get("2"))) {
            END_ALL_KEYWORD.addAll(dictMap.get("2").stream().map(Config::getValue).collect(Collectors.toSet()));
        }
        if (CollectionUtils.isNotEmpty(dictMap.get("3"))) {
            QUERY_KEYWORD.addAll(dictMap.get("3").stream().map(Config::getValue).collect(Collectors.toSet()));
        }
        if (CollectionUtils.isNotEmpty(dictMap.get("4"))) {
            BLACK_NAME.addAll(dictMap.get("4").stream().map(Config::getValue).collect(Collectors.toSet()));
        }

    }


    /**
     * @param wxid
     * @param msg
     * @param userId
     * @return "0" 进行下一步
     * "" 不做任何处理，流程终止
     * 字符串，发送到微信
     */
    public String action(String wxid, String msg, Long userId) {


        if (msg.matches("^[-\\+]?[\\d]*$")) {

            if (Long.parseLong(msg) < 100) {
                // 特殊编号
                // 暂不做处理

                if (Long.parseLong(msg) == 2){
                    getOrders(userId,wxid);
                }

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
                return "下单成功";
            } else {
                return "下单失败\r" + response.getMessage();
            }
            // } else if (Objects.equals("全部下号", msg)) {
        } else if (END_ALL_KEYWORD.stream().anyMatch(msg::contains)) {
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
                int index = 0;
                for (int i = 0; i < msg.length(); i++) {
                    if (Character.isDigit(msg.charAt(i))) {
                        index = i;
                        break;
                    }
                }
                accountName = msg.substring(index, msg.indexOf("----"));
            } else if (msg.contains("---")) {
                int index = 0;
                for (int i = 0; i < msg.length(); i++) {
                    if (Character.isDigit(msg.charAt(i))) {
                        index = i;
                        break;
                    }
                }
                accountName = msg.substring(index, msg.indexOf("---"));
            } else if (msg.matches("[0-9]+.*")) {
                // TODO: 2020/11/12  这里的逻辑貌似有点问题，一直走不到这里来
                int index = 0;
                for (int i = 0; i < msg.length(); i++) {
                    if (!Character.isDigit(msg.charAt(i))) {
                        index = i;
                        break;
                    }
                }
                accountName = msg.substring(0, index);
            }

            if (StringUtils.isEmpty(accountName)) {
                return "查询格式有误\n 请输入: 用户名---密码 是否可用 查询账号信息";
            }

            GetAccountDto dto = GetAccountDto.builder()
                    .accountName(accountName)
                    .build();

            BaseResponse response = accountService.getAccounts(dto);

            if (response.getData() == null) {
                return "没有查到 " + accountName + " 该账号信息";
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
        }else if (msg.equals("订单列表")){
            getOrders(userId,wxid);
            return "";
        }

        return "0";
    }

    private void getOrders(Long userId,String wxid){
        List<Orders> orders = ordersDao.findByUserIdAndStatus(userId, "1");

        List<Account> accounts = accountDao.findByIdIn(orders.stream().map(Orders::getAccountId).collect(Collectors.toList()));

        Map<Long, Account> accountMap = Maps.uniqueIndex(accounts, Account::getId);


        StringBuilder builder = new StringBuilder();
        builder.append("当前共有 ");
        builder.append(orders.size());
        builder.append("个 账号正在租赁中\r");
        Date date = new Date();
        orders.forEach( order -> {
            Account account = accountMap.get(order.getAccountId());
            builder.append("【"+order.getAccountId()+"】 ");
            builder.append(account.getServer());
            builder.append(" ");
            builder.append(account.getNickName());
            int times = getTimeDifference(date, order.getStartTime());
            builder.append(" 时长 ");
            builder.append(times);
            builder.append(" 分钟\r");
        });

        InstructionUtil.sendText(wxid,builder.toString());


    }

    private String rent(RentDto dto) {
        BaseResponse response = ordersService.rent(dto);
        if (Objects.equals(response.getCode(), 200)) {
            return response.getData().toString();
        } else {
            return response.getMessage();
        }
    }

    public static void main(String[] args) {
        String content = "<msg fromusername=\"wxid_50v93ju2u3b622\" encryptusername=\"v3_020b3826fd03010000000000d39631e2e17f49000000501ea9a3dba12f95f6b60a0536a1adb634399dbce837863888927c47a4d0da136d483e512e590e4c10b17664ce7d578aacd861621630669c0df36bb382b58b70bc1f16ed56ec839d8fbf03a01a@stranger\" fromnickname=\"亦髯\" content=\"我是群聊“机器人测试”的亦髯\" fullpy=\"yiran\" shortpy=\"YR\" imagestatus=\"3\" scene=\"14\" country=\"\" province=\"\" city=\"\" sign=\"\" percard=\"1\" sex=\"0\" alias=\"xly19940409\" weibo=\"\" albumflag=\"0\" albumstyle=\"0\" albumbgimgid=\"\" snsflag=\"256\" snsbgimgid=\"\" snsbgobjectid=\"0\" mhash=\"f103580baae60c2a8fc26c27bf129e19\" mfullhash=\"f103580baae60c2a8fc26c27bf129e19\" bigheadimgurl=\"http://wx.qlogo.cn/mmhead/ver_1/Z3re4NYghDwl23sJvRYwpyS7IPtp0khOJempZxm88r1ELdNEvmt4WANukGfiaxSgMbU7lFk4LbIF32fC6PticN2Tv6AuZutjFhx8wYSaiaW088/0\" smallheadimgurl=\"http://wx.qlogo.cn/mmhead/ver_1/Z3re4NYghDwl23sJvRYwpyS7IPtp0khOJempZxm88r1ELdNEvmt4WANukGfiaxSgMbU7lFk4LbIF32fC6PticN2Tv6AuZutjFhx8wYSaiaW088/96\" ticket=\"v4_000b708f0b040000010000000000d8be73144d2a0314b55965a2cb5f1000000050ded0b020927e3c97896a09d47e6e9e96d75d4004990754c48e4ca4448f1fc94a017d4f9abc76530209fbafb4c492c129a056f49def39572845f33b09a504653321f7cc7afe04792eae1a2df084d562fbc18370c402509bc200a758c0f237c8dd4340892b16146cadb4937e897abcdcea6293517cac45f8df@stranger\" opcode=\"2\" googlecontact=\"\" qrticket=\"\" chatroomusername=\"19621412245@chatroom\" sourceusername=\"\" sourcenickname=\"\" sharecardusername=\"\" sharecardnickname=\"\" cardversion=\"\"><brandlist count=\"0\" ver=\"684001083\"></brandlist></msg>";

        // String encryptusername = XmlUtils.getNodeValue("encryptusername", content);

        String attributes = XmlUtils.getAttributes(content, "encryptusername", "ticket");
        System.out.println(attributes);
    }

}
