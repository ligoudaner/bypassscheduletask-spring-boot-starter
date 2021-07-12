package com.fintek.bypassscheduletask.spring.boot.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: xiangwei
 * @CreateDate: 6/11/21 3:01 PM
 */
@Configuration
@ConfigurationProperties(prefix = "bypass-schedule-task")
public class ScheduleUtilProperties {

    private String tableName;

    private boolean autoCreateTable=true;

    private Integer scheduleThreadCount;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isAutoCreateTable() {
        return autoCreateTable;
    }

    public void setAutoCreateTable(boolean autoCreateTable) {
        this.autoCreateTable = autoCreateTable;
    }

    public Integer getScheduleThreadCount() {
        return scheduleThreadCount;
    }

    public void setScheduleThreadCount(Integer scheduleThreadCount) {
        this.scheduleThreadCount = scheduleThreadCount;
    }
}
