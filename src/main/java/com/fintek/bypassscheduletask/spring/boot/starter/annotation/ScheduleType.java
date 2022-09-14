package com.fintek.bypassscheduletask.spring.boot.starter.annotation;

import java.lang.annotation.*;

/**
 * @Author: Sven
 * @CreateDate: 2022-09-14 09:52
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScheduleType {
    String value() default "";
}
