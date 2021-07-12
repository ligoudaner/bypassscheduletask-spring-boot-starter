package com.fintek.bypassscheduletask.spring.boot.starter;

import com.fintek.bypassscheduletask.spring.boot.starter.base.ScheduleIdHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @Description:
 * @Author: xiangwei
 * @CreateDate: 2020-03-12 11:45
 */
@Slf4j
public abstract class ScheduleFixedDelayHandler<T extends ScheduleIdHolder> extends ScheduleHandler<T> {

    /**
     * 控制方法执行的间隔时间，是以上一次方法执行完开始算起，
     * 如上一次方法执行阻塞住了，那么直到上一次执行完，
     * 并间隔给定的时间后，执行下一次。
     *
     * @return 以毫秒为单位
     */
    public abstract long fixedDelay();

    public long initialDelay() {
        return 0;
    }

    @Override
    public void addTask(ScheduledTaskRegistrar scheduledTaskRegistrar, ScheduleHandler scheduleHandler) {
        log.info("创建job-,{},fixedDelay-{},initialDelay-{}", getClass().getSimpleName(), fixedDelay(), initialDelay());
        scheduledTaskRegistrar.addFixedDelayTask(new IntervalTask(scheduleHandler, fixedDelay(), initialDelay()));
    }
}
