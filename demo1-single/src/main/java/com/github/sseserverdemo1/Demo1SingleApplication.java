package com.github.sseserverdemo1;

import com.github.sseserver.local.LocalConnectionService;
import com.github.sseserverdemo1.model.MyAccessUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 单机版，不依赖nacos
 */
@SpringBootApplication
public class Demo1SingleApplication {
    private static final Logger log = LoggerFactory.getLogger(Demo1SingleApplication.class);
    @Resource
    private LocalConnectionService localConnectionService;

    public static void main(String[] args) {
        SpringApplication.run(Demo1SingleApplication.class, args);
        log.info("请访问 http://127.0.0.1:8080 进行观察");
    }

    @PostConstruct
    public void init() {
        addUserOnlineListener();
        mockProductSendEvent();
    }

    private void addUserOnlineListener() {
        localConnectionService.<MyAccessUser>addConnectListener(conn -> {
            MyAccessUser user = conn.getAccessUser();
            log.info("user online {}", user);
        });
    }

    private void mockProductSendEvent() {
        new ScheduledThreadPoolExecutor(1)
                .scheduleWithFixedDelay(() -> {
                    // 每5秒发送消息
                    List<MyAccessUser> users = localConnectionService.getUsers();
                    for (MyAccessUser user : users) {
                        localConnectionService.sendByUserId(user.getId(),
                                "server-push", "{\"name\":\"sendByUserId 服务端推送的\"}");
                    }
                }, 1, 3, TimeUnit.SECONDS);
    }

}
