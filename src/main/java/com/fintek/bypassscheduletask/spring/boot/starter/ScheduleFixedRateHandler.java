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
public abstract class ScheduleFixedRateHandler<T extends ScheduleIdHolder> extends ScheduleHandler<T> {

    /**
     * 是按照一定的速率执行，是从上一次方法执行开始的时间算起，
     * 如果上一次方法阻塞住了，下一次也是不会执行，
     * 但是在阻塞这段时间内累计应该执行的次数，当不再阻塞时，
     * 一下子把这些全部执行掉，而后再按照固定速率继续执行。
     *
     * @return 以毫秒为单位
     */
    public abstract long fixedRate();

    public long initialDelay() {
        return 0;
    }

    @Override
    public void addTask(ScheduledTaskRegistrar scheduledTaskRegistrar, ScheduleHandler scheduleHandler) {
        log.info("创建job-,{},fixedRate-{},initialDelay-{}", getClass().getSimpleName(), fixedRate(), initialDelay());
        scheduledTaskRegistrar.addFixedRateTask(new IntervalTask(scheduleHandler, fixedRate(), initialDelay()));
    }
}
