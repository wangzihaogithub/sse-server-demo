package com.github.sse.dubbo2;

import com.github.sse.model.MyAccessUser;
import com.github.sseserver.DistributedConnectionService;
import com.github.sseserver.SendService;
import com.github.sseserver.remote.ClusterConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 假设这里是一个dubbo应用，产生了用户事件
 */
@SpringBootApplication
public class Demo2dDistributedDubbo2Application {
    private static final Logger log = LoggerFactory.getLogger(Demo2dDistributedDubbo2Application.class);
    @Resource
    private DistributedConnectionService distributedConnectionService;

    @PostConstruct
    public void init() {
        mockProductSendEvent();
    }

    private void mockProductSendEvent() {
        new ScheduledThreadPoolExecutor(1)
                .scheduleWithFixedDelay(() -> {
                    // 每5秒发送消息
                    ClusterConnectionService cluster = distributedConnectionService.getCluster();

                    cluster.<MyAccessUser>getUsersAsync().whenComplete((users, throwable) -> {
                        sendByUserId(distributedConnectionService.getCluster(), users).whenComplete((sendResult, sendThrowable) -> {
                            log.info("sendByUserId result = {}", sendResult, sendThrowable);
                        });
//                        sendByUserId(distributedConnectionService.qos(), users);
                    });
                }, 1, 3, TimeUnit.SECONDS);
    }

    private static <RESPONSE> RESPONSE sendByUserId(SendService<RESPONSE> sendService, List<MyAccessUser> users) {
        log.info("sendByUserId = {}", users);
        return sendService.sendByUserId(users.stream().map(MyAccessUser::getId).collect(Collectors.toList()),
                "server-push", "{\"name\":\"sendByUserId dubbo2号推送的\"}");
    }

    public static void main(String[] args) {
        SpringApplication.run(Demo2dDistributedDubbo2Application.class, args);
    }

}
