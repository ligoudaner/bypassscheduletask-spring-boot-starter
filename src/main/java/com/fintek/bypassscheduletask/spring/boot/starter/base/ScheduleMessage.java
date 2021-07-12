package com.fintek.bypassscheduletask.spring.boot.starter.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description:
 * @Author: xiangwei
 * @CreateDate: 2020-03-09 14:34
 */
@NoArgsConstructor
@Data
@ToString
@AllArgsConstructor
public class ScheduleMessage {
    private Long id;
    private String orderId;
    private String type;
    private String tableName;
    private String serverId;
    private Integer version;
}
