package com.fintek.bypassscheduletask.spring.boot.starter.conf;

import javax.sql.DataSource;

/**
 * @author zhangyongjia
 */
public class WorkerConfiguration {

    //默认的本地数据库表名称
    protected static final String DEFAULT_MQ_TABLE_NAME = "schedule_message";

    //默认的线程数
    protected static final int DEFAULT_SCHEDULE_THREAD_COUNT = 5;

    protected Integer scheduleThreadCount = DEFAULT_SCHEDULE_THREAD_COUNT;

    protected String serviceId;
    protected String instanceId;

    protected String tableName = DEFAULT_MQ_TABLE_NAME;
    protected DataSource dataSource;
    protected boolean autoCreateTable = false;

    private WorkerConfiguration() {
    }

    public WorkerConfiguration(String tableName, DataSource dataSource, boolean autoCreateTable, Integer scheduleThreadCount) {
        if (null == dataSource) throw new IllegalArgumentException("dataSource must not be null");
        this.dataSource = dataSource;
        if (null != tableName) this.tableName = tableName;
        if (autoCreateTable) this.autoCreateTable = true;
        if (null != scheduleThreadCount) this.scheduleThreadCount = scheduleThreadCount;
    }

    public String getTableName() {
        return tableName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public boolean isAutoCreateTable() {
        return autoCreateTable;
    }
}
