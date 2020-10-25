package com.harry.wechat.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Harry on 2017/8/8.
 */
@Component
public class SQLSupporter {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Object[]> queryBySql(String sql) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> result = query.getResultList();
        EntityManagerFactoryUtils.closeEntityManager(entityManager);
        return result;
    }
    public <T> List<T> queryBySql(String sql, Class T) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query = entityManager.createNativeQuery(sql, T);
        List<T> result = query.getResultList();
        EntityManagerFactoryUtils.closeEntityManager(entityManager);
        return result;
    }
    public void updateByBatchSql(String sql){
        jdbcTemplate.batchUpdate(sql.split(";"));
    }

    public int updateSql(String sql){
        return jdbcTemplate.update(sql);
    }

    public void execute(String sql){
        jdbcTemplate.execute(sql);
    }
}
