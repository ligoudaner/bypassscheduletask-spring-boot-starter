# bypassscheduletask-spring-boot-starter
eureka多服务定时任务分流消费

例如：支付服务有N个实例，这N个实例各自执行定时任务，但操作的数据不会重复

和xxl-job的分片处理类似

maven:
```xml
        <dependency>
            <groupId>com.fintek</groupId>
            <artifactId>bypassscheduletask-spring-boot-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```
application.yml 配置
```yml
bypass-schedule-task:
    tableName: sys_schedule_message
```

```java

import com.fintek.bypassscheduletask.spring.boot.starter.annotation.ScheduleType;

@Service
@Slf4j
@ScheduleType("TestJob")
public class TestJob extends ScheduleFixedRateHandler<? extends ScheduleIdHolder> {


    @Override
    public long fixedRate() {
        return 1000 * 60 * 30;
    }

    @Override
    protected boolean process(? scheduleIdHolder) {
        /**
         * 业务处理
         */
        return true;
    }

    /**
     *
     * @param instancesTotal 实例总数
     * @param currentNumber 当前位置
     * @return 需要处理的数据集合
     */
    @Override
    protected List<?> getData(Integer instancesTotal, Integer currentNumber) {
        return 业务查询(instancesTotal, currentNumber);
    }
}
```
