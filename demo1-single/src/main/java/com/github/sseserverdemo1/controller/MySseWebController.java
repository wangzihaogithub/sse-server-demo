package com.github.sseserverdemo1.controller;

import com.github.sseserver.local.SseEmitter;
import com.github.sseserver.local.SseWebController;
import com.github.sseserverdemo1.model.MyAccessUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/my-sse")
public class MySseWebController extends SseWebController<MyAccessUser> {
    private static final Logger log = LoggerFactory.getLogger(MySseWebController.class);
    @Autowired
    private HttpServletRequest request;
    @Value("${server.port:8080}")
    private Integer port;

    @Override
    protected Object onMessage(String path, SseEmitter<MyAccessUser> connection, Map<String, Object> message) {
        if ("close".equals(path)) {
            connection.disconnect();
        } else {
            log.info("onMessage = {}", message);
        }
        return 1;
    }

    @Override
    protected Object onUpload(String path, SseEmitter<MyAccessUser> connection, Map<String, Object> message, Collection<Part> files) {
        return "111";
    }

    @Override
    protected MyAccessUser getAccessUser(String api) {
        // 验证用户
        MyAccessUser accessUser = new MyAccessUser();
        accessUser.setId(port + "");
        accessUser.setAccessToken(port + "_" + request.getParameter("access-token"));
        accessUser.setTenantId(port);
        return accessUser;
    }

}
