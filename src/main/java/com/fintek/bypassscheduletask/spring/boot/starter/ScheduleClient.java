package com.fintek.bypassscheduletask.spring.boot.starter;

import com.fintek.bypassscheduletask.spring.boot.starter.conf.WorkerConfiguration;
import com.fintek.bypassscheduletask.spring.boot.starter.mybatis.MybatisSqlSessionFactory;
import com.fintek.bypassscheduletask.spring.boot.starter.base.ScheduleMessageMapper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleClient {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleClient.class);

    private WorkerConfiguration workerConfiguration;
    private MybatisSqlSessionFactory mybatisSqlSessionFactory;

    public ScheduleClient(WorkerConfiguration workerConfiguration) {
        this.workerConfiguration = workerConfiguration;
        mybatisSqlSessionFactory = new MybatisSqlSessionFactory(workerConfiguration.getDataSource());
        if (workerConfiguration.isAutoCreateTable()) {
            SqlSession sqlSession = mybatisSqlSessionFactory.getSqlSession();
            ScheduleMessageMapper scheduleMessageMapper = sqlSession.getMapper(ScheduleMessageMapper.class);
            try {
                scheduleMessageMapper.createTable(workerConfiguration.getTableName());
            } finally {
                sqlSession.close();
            }
        }
    }

    public void setServiceConfig(String serviceId, String instanceId) {
        this.getWorkerConfiguration().setServiceId(serviceId);
        this.getWorkerConfiguration().setInstanceId(instanceId);
    }

    public WorkerConfiguration getWorkerConfiguration() {
        return workerConfiguration;
    }

    public MybatisSqlSessionFactory getMybatisSqlSessionFactory() {
        return mybatisSqlSessionFactory;
    }
}
