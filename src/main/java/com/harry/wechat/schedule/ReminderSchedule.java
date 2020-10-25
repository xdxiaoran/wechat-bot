package com.harry.wechat.schedule;

import com.google.common.collect.Maps;
import com.harry.wechat.dao.OrdersDao;
import com.harry.wechat.dao.UserInfoDao;
import com.harry.wechat.entity.Orders;
import com.harry.wechat.entity.UserInfo;
import com.harry.wechat.util.InstructionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.harry.wechat.util.DateUtils.getTimeDifference;

/**
 * @author Harry
 * @date 2020/10/24
 * Time: 17:51
 * Desc: ReminderSchedule
 */
@Component
@Slf4j
public class ReminderSchedule {

    @Autowired
    private OrdersDao ordersDao;
    @Autowired
    private UserInfoDao userInfoDao;

    @Scheduled(cron = "${schedule.cron}")
    @Transactional
    public void reminder() {
        log.info("开始扫描订单信息...");

        try {
            List<Orders> orders = ordersDao.findByStatusAndRemindStatus("1", false);
            if (CollectionUtils.isNotEmpty(orders)) {
                // List<Long> userIds = orders.stream().map(Orders::getUserId).collect(Collectors.toList());
                // List<UserInfo> userList = userInfoDao.findByIdIn(userIds);
                List<UserInfo> userList = userInfoDao.findAll();
                Date date = new Date();

                // Map<Long, String> userMap = userList.stream().collect(Collectors.toMap(UserInfo::getId, UserInfo::getWxid, (k1, k2) -> k1));

                Map<Long, UserInfo> userMap = Maps.uniqueIndex(userList, UserInfo::getId);

                List<Orders> forUpdate = new ArrayList<>();
                orders.forEach(order -> {
                    int cost = getTimeDifference(date, order.getStartTime());

                    UserInfo userInfo = userMap.get(order.getUserId());
                    if (userInfo != null) {
                        Integer remindTime = userInfo.getRemindTime();
                        if (remindTime == null) {
                            remindTime = 360;
                        }

                        if (cost > remindTime) {
                            // 已经超过设定时间没有下号
                            // 发消息提示
                            InstructionUtil.sendText(userInfo.getWxid(), "您有账号租赁已经超过 " + Math.floor(cost / 60) + "小时，请注意");
                            order.setRemindStatus(true);
                            forUpdate.add(order);
                        }
                    }

                });

                if (CollectionUtils.isNotEmpty(forUpdate)) {
                    ordersDao.saveAll(forUpdate);
                    log.info("提示" + forUpdate.size() + "人");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("提示失败");
            log.error("提示失败 ",e);

        }

        log.info("扫描结束");

    }
}
