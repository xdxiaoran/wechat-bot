package com.harry.wechat.dao;

import com.harry.wechat.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 18:01
 * Desc: OrdersDao
 */
public interface OrdersDao extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {

    List<Orders> findByAccountIdAndStatusNot(Long accountId, String status);


    List<Orders> findByUserIdAndStatus(Long userId, String status);

    List<Orders> findByStatusAndRemindStatus(String status, Boolean remindStatus);

    List<Orders> findByAccountIdInAndStatusNot(List<Long> accountIds, String status);
}
