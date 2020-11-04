package com.harry.wechat.dao;

import com.harry.wechat.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 18:01
 * Desc: AccountDao
 */
public interface AccountDao extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    Account findByIdAndStatus(Long id, int status);

    Account findByUsername(String username);

    List<Account> findByIdIn(List<Long> accountIds);

    List<Account> findByStatus(Integer status);
}
