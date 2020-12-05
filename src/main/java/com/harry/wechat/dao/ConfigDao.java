package com.harry.wechat.dao;

import com.harry.wechat.entity.Config;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Harry
 * @date 2020/11/23
 * Time: 22:29
 * Desc: ConfigDao
 */
public interface ConfigDao extends JpaRepository<Config, Long> {

    Page<Config> findByLabLike(String lab, Pageable pageRequest);
}
