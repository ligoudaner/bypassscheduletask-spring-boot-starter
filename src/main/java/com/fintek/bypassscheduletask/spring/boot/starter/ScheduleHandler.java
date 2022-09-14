package com.fintek.bypassscheduletask.spring.boot.starter;

import com.fintek.bypassscheduletask.spring.boot.starter.annotation.ScheduleType;
import com.fintek.bypassscheduletask.spring.boot.starter.base.ScheduleIdHolder;
import com.fintek.bypassscheduletask.spring.boot.starter.base.ScheduleMessage;
import com.fintek.bypassscheduletask.spring.boot.starter.base.ScheduleMessageMapper;
import com.fintek.bypassscheduletask.spring.boot.starter.conf.WorkerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: xiangwei
 * @CreateDate: 2020-03-09 16:59
 */
@Slf4j
public abstract class ScheduleHandler<T extends ScheduleIdHolder> implements Runnable {

    @Autowired
    private ScheduleClient scheduleClient;

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 业务处理
     *
     * @param arg
     * @return boolean 返回true，表示业务处理成功，false表示处理失败，下次任务有同一条数据会继续执行
     */
    protected abstract boolean process(T arg);

    /**
     * 获取需要处理的数据
     *
     * @param instancesTotal 实例总数
     * @param currentNumber  当前实例编号
     * @return
     */
    protected abstract List<T> getData(Integer instancesTotal, Integer currentNumber);

    public abstract void addTask(ScheduledTaskRegistrar scheduledTaskRegistrar, ScheduleHandler<T> scheduleHandler);


    @Override
    public void run() {
        WorkerConfiguration workerConfiguration = scheduleClient.getWorkerConfiguration();
        Integer serviceInstanceNum = getServiceInstanceNum(workerConfiguration.getServiceId());
        Integer serviceIpPortListIndex = -1;
        if (serviceInstanceNum > 0) {
            serviceIpPortListIndex = getServiceIpPortListIndex(workerConfiguration.getServiceId(), workerConfiguration.getInstanceId());
            if (serviceIpPortListIndex < 0) {
                if (log.isDebugEnabled()) {
                    log.info("当前服务位置小于0，不做业务查询:{}", serviceIpPortListIndex);
                }
                return;
            }
            if (log.isDebugEnabled()) {
                log.info("当前服务所在总服务位置:{}", serviceIpPortListIndex);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.info("没找到服务~");
            }
            return;
        }
        List<T> data = getData(serviceInstanceNum, serviceIpPortListIndex);
        if (null == data) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.info("数据大小-{}", data.size());
        }
        SqlSession sqlSession = scheduleClient.getMybatisSqlSessionFactory().getSqlSessionFactory().openSession(ExecutorType.SIMPLE, false);
        ScheduleMessageMapper scheduleMessageMapper = sqlSession.getMapper(ScheduleMessageMapper.class);
        String tableName = workerConfiguration.getTableName();

        ScheduleType scheduleType = this.getClass().getAnnotation(ScheduleType.class);

        try {
            for (T t : data) {
                boolean flag = false;
                ScheduleMessage scheduleMessage = null;
                try {
                    scheduleMessage = scheduleMessageMapper.selectByOrderIdAndType(tableName, t.getScheduleId(), scheduleType.value());
                    if (scheduleMessage == null) {
                        scheduleMessage = new ScheduleMessage();
                        scheduleMessage.setOrderId(t.getScheduleId());
                        scheduleMessage.setType(scheduleType.value());
                        scheduleMessage.setServerId(workerConfiguration.getInstanceId());
                        scheduleMessage.setTableName(tableName);
                        scheduleMessage.setVersion(0);
                        if (scheduleMessageMapper.insert(scheduleMessage) <= 0) {
                            flag = true;
                            log.error("failed to insert record");
                            throw new RuntimeException("failed to insert record");
                        }
                    }
                    if (scheduleMessage.getVersion() != -1 && scheduleMessageMapper.compareAndSet(tableName, scheduleMessage.getId(), scheduleMessage.getVersion(), -1) > 0) {
                        flag = process(t);
                    } else {
                        flag = true;
                        log.error("no permission");
                        throw new RuntimeException("no permission");
                    }
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.error(e.getMessage());
                    }
                    e.printStackTrace();
                }
                if (!flag) {
                    if (scheduleMessage != null) {
                        scheduleMessage.setTableName(tableName);
                        //如果返回false，版本号+1,表示这条任务执行失败，下次可以继续执行
                        scheduleMessageMapper.updateVersionACC(scheduleMessage);
                    }
                }
            }
        } finally {
            sqlSession.close();
        }
    }

    private Integer getServiceInstanceNum(String serviceId) {
        if (StringUtils.isEmpty(serviceId)) {
            return 0;
        }
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
        if (null == serviceInstances || serviceInstances.size() < 1) {
            return 0;
        }
        if (log.isDebugEnabled()) {
            log.info("当前注册数：" + serviceInstances.size());
        }
        return serviceInstances.size();
    }

    private Integer getServiceIpPortListIndex(String serviceId, String instanceId) {
        if (StringUtils.isEmpty(serviceId) || StringUtils.isEmpty(instanceId)) {
            return 0;
        }
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
        if (null == serviceInstances || serviceInstances.size() < 1) {
            return 0;
        }
        List<String> ipPortEureka = serviceInstances.stream().map(serviceInstance -> serviceInstance.getHost() + ":" + serviceInstance.getPort()).sorted().collect(Collectors.toList());
        int index = ipPortEureka.indexOf(instanceId);
        if (log.isDebugEnabled()) {
            log.info("处在当前列表第：{}", (index));
        }
        return index;
    }
}
