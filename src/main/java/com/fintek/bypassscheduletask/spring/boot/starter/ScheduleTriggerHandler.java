package com.fintek.bypassscheduletask.spring.boot.starter;

import com.fintek.bypassscheduletask.spring.boot.starter.base.ScheduleIdHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.StringUtils;

/**
 * @Description:
 * @Author: xiangwei
 * @CreateDate: 2020-03-12 11:45
 */
@Slf4j
public abstract class ScheduleTriggerHandler<T extends ScheduleIdHolder> extends ScheduleHandler<T> {

    /**
     * @return 可以解析为cron计划的表达式
     * @Description: 类似cron的表达式，扩展了通常的UN * X定义以包括在秒，分钟，小时，月份，月份中触发和星期几。
     * 例如 {@code "0 * * * * MON-FRI"}表示每分钟开启一次工作日（分钟的顶部-第0秒）。
     */
    public abstract String getCron();

    /**
     * 将解决cron表达式的时区。 默认情况下，
     * 属性为空字符串（即将使用服务器的本地时区）。
     *
     * @return 接受的区域ID {@link java.util.TimeZone＃getTimeZone（String）}或空字符串以指示服务器的默认时区
     */
    public String zone() {
        return "";
    }

    @Override
    public void addTask(ScheduledTaskRegistrar scheduledTaskRegistrar, ScheduleHandler scheduleHandler) {
        log.info("创建job-,{},cron{}", getClass().getSimpleName(), getCron());
        scheduledTaskRegistrar.addTriggerTask(scheduleHandler, triggerContext -> {
            CronTrigger cronTrigger = null;
            try {
                if (StringUtils.hasText(zone())) {
                    cronTrigger = new CronTrigger(getCron(), StringUtils.parseTimeZoneString(zone()));
                } else {
                    cronTrigger = new CronTrigger(getCron());
                }
            } catch (Exception e) {
                log.error("创建job-{}失败，cron{}", this.getClass().getSimpleName(), this.getCron(), e);
                throw new RuntimeException("创建job错误，检查cron表达式是否正确");
            }
            return cronTrigger.nextExecutionTime(triggerContext);
        });
    }
}
