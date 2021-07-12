package com.fintek.bypassscheduletask.spring.boot.starter.base;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:
 * @Author: xiangwei
 * @CreateDate: 2020-03-09 14:34
 */
public interface ScheduleMessageMapper {

    int insert(ScheduleMessage scheduleMessage);

    ScheduleMessage selectById(@Param("tableName") String tableName, @Param("id") Long id);

    ScheduleMessage selectByOrderIdAndType(@Param("tableName") String tableName, @Param("orderId") String orderId, @Param("type") String type);

    int compareAndSet(@Param("tableName") String tableName, @Param("id") Long id, @Param("expect") Integer expect, @Param("update") Integer update);

    /**
     * 根据orderId、type，累加version
     *
     * @param scheduleMessage
     * @return
     */
    int updateVersionACC(ScheduleMessage scheduleMessage);

    int createTable(@Param("tableName") String tableName);
}
