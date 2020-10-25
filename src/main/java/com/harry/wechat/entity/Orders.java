package com.harry.wechat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Harry
 * @date 2020/10/9
 * Time: 14:16
 * Desc: Orders
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orders extends BaseEntity {
    private Long userId;
    private Long accountId;

    @Column(name = "start_time",insertable = false,
            updatable = false,
            nullable = false,
            columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP")
    private Date startTime;

    @Column(name = "end_time",columnDefinition = "datetime")
    private Date endTime;
    /**
     * 备注
     * 后台修改状态时添加
     */
    private String mark;

    /**
     * 租赁时长
     * 单位 分钟
     */
    private Integer costTime;
    /**
     * 应付金额
     */
    private BigDecimal amount;
    /**
     * 支付状态
     * 0 已支付
     * 1 待支付
     */
    private String payStatus;

    /**
     * 订单状态
     * 0 结束
     * 1 进行中
     * 2 完成
     * 3 其它
     */
    @Column(name = "status",columnDefinition = "varchar(10) default 1")
    private String status;

    /**
     * 是否提醒过
     */
    private Boolean remindStatus;
}
