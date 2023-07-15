package com.github.sse.tomcat2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 集群多实例版，依赖nacos
 */
@SpringBootApplication
public class Demo2dDistributedTomcat2Application {
    private static final Logger log = LoggerFactory.getLogger(Demo2dDistributedTomcat2Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Demo2dDistributedTomcat2Application.class, args);
        log.info("请访问 http://127.0.0.1:8081 进行观察");
    }

}
