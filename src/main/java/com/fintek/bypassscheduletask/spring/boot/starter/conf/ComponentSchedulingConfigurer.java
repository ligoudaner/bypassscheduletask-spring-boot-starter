package com.fintek.bypassscheduletask.spring.boot.starter.conf;

import com.fintek.bypassscheduletask.spring.boot.starter.ScheduleClient;
import com.fintek.bypassscheduletask.spring.boot.starter.ScheduleHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Description:
 * @Author: xiangwei
 * @CreateDate: 2020-03-10 10:23
 */
@Component
@EnableScheduling
@Slf4j
public class ComponentSchedulingConfigurer implements SchedulingConfigurer {

    @Autowired
    private List<ScheduleHandler> scheduleHandlers;

    /**
     * 服务注册名称
     */
    @Value("${spring.application.name:}")
    private String serviceId;

    /**
     * 服务注册实例id
     */
    @Value("${eureka.instance.instance-id:}")
    private String instanceId;
    @Value("${spring.cloud.client.ipAddress:}")
    private String ip_v1;
    @Value("${spring.cloud.client.ip-address:}")
    private String ip_v2;
    @Value("${server.port}")
    private String port;

    @Autowired
    private ScheduleClient scheduleClient;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        if (StringUtils.isEmpty(instanceId)) {
            if (StringUtils.isNotEmpty(ip_v1)) {
                instanceId = ip_v1 + ":" + serviceId + ":" + port;
            } else if (StringUtils.isNotEmpty(ip_v2)) {
                instanceId = ip_v2 + ":" + serviceId + ":" + port;
            }
        }
        scheduleClient.setServiceConfig(serviceId, instanceId);
        //创建一个线程池调度器，默认是单线程执行
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(scheduleClient.getWorkerConfiguration().scheduleThreadCount);
        scheduledTaskRegistrar.setScheduler(executorService);
        scheduleHandlers.stream().forEach(scheduleHandler -> scheduleHandler.addTask(scheduledTaskRegistrar, scheduleHandler));
    }
}
