package com.harry.wechat.dao;

import com.harry.wechat.entity.Recharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 18:01
 * Desc: AccountDao
 */
public interface RechargeDao extends JpaRepository<Recharge, Long>, JpaSpecificationExecutor<Recharge> {
}
