package com.fintek.bypassscheduletask.spring.boot.starter;

import com.fintek.bypassscheduletask.spring.boot.starter.conf.ComponentSchedulingConfigurer;
import com.fintek.bypassscheduletask.spring.boot.starter.conf.WorkerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

/**
 * @Description:
 * @Author: xiangwei
 * @CreateDate: 6/11/21 11:53 AM
 */
@Configuration
@Import({ComponentSchedulingConfigurer.class})
@EnableConfigurationProperties(ScheduleUtilProperties.class)
public class ScheduleUtilAutoConfiguration {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private ScheduleUtilProperties scheduleUtilProperties;

    @Bean
    public ScheduleClient schedulingClient() {
        WorkerConfiguration workerConfiguration = new WorkerConfiguration(scheduleUtilProperties.getTableName(), dataSource, scheduleUtilProperties.isAutoCreateTable(), scheduleUtilProperties.getScheduleThreadCount());
        return new ScheduleClient(workerConfiguration);
    }
}
