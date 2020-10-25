package com.harry.wechat.dao;

import com.harry.wechat.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 18:01
 * Desc: AccountDao
 */
public interface UserInfoDao extends JpaRepository<UserInfo, Long>, JpaSpecificationExecutor<UserInfo> {
    Optional<UserInfo> findByWxid(String wxid);

    List<UserInfo> findByIdIn(List<Long> userIds);
}
