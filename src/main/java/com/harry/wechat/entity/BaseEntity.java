package com.harry.wechat.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Harry
 * @date 2020/9/26
 * Time: 17:48
 * Desc: BaseEntity
 */
@MappedSuperclass
@Data
public class BaseEntity {
    /**
     * primary key to identify a record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * the time when save this record into database.
     * set data type to be timestamp and default value to be CURRENT_TIMESTAMP.
     * set insertable to be false to make sure that this field will not be included in SQL INSERT statements
     * when the instance is saved by hibernate, therefore we can make sure it is maintained by database.
     */
    @Column(insertable = false,
            updatable = false,
            nullable = false,
            columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP")
    private Date createTime;

    /**
     * last update time of this record.
     * set data type to be timestamp and default value to be CURRENT_TIMESTAMP,
     * also, update it to be CURRENT_TIMESTAMP when any of record field is updated.
     * set insertable to be false to make sure that this field will not be included in SQL INSERT statements
     * when the instance is saved by hibernate, therefore we can make sure it is maintained by database.
     */
    @Column(insertable = false,
            updatable = false,
            nullable = false,
            columnDefinition = "timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date lastUpdateTime;
}
