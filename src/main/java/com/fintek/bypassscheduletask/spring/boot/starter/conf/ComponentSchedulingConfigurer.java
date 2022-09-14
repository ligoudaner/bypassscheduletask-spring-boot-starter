package com.fintek.bypassscheduletask.spring.boot.starter.conf;

import com.fintek.bypassscheduletask.spring.boot.starter.ScheduleClient;
import com.fintek.bypassscheduletask.spring.boot.starter.ScheduleHandler;
import com.fintek.bypassscheduletask.spring.boot.starter.annotation.ScheduleType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private Set<String> scheduleTypeSet = new HashSet<>();

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
        scheduleHandlers.forEach(scheduleHandler -> {
            ScheduleType scheduleType = scheduleHandler.getClass().getAnnotation(ScheduleType.class);
            if (scheduleType == null || StringUtils.isBlank(scheduleType.value())) {
                log.error("定时Service类必须要@ScheduleType()注解，并且唯一");
                System.exit(-1);
            } else {
                if (!scheduleTypeSet.contains(scheduleType.value())) {
                    scheduleTypeSet.add(scheduleType.value());
                } else {
                    log.error("定时Service类的@ScheduleType(\"{}\")注解值不唯一", scheduleType.value());
                    System.exit(-1);
                }
            }
            scheduleHandler.addTask(scheduledTaskRegistrar, scheduleHandler);
        });
    }
}
