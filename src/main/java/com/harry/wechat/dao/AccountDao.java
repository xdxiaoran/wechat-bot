package com.harry.wechat.dao;

import com.harry.wechat.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

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

    @Query(value = "select a.id,a.`server`,a.username,a.nick_name,count(1) count,sum(o.cost_time) times,sum(o.amount) amount from account a " +
            "inner join orders o on a.id = o.account_id " +
            "where o.status = 0 " +
            "and o.create_time BETWEEN ?1 and ?2 " +
            "group by a.username", nativeQuery = true)
    List<Map<String,Object>> findBySql(String start, String end);
}
