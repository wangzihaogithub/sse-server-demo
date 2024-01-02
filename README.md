# sse-server-demo

#### 介绍

演示sse-server项目如何使用

    <!-- https://mvnrepository.com/artifact/com.github.wangzihaogithub/sse-server -->
    <dependency>
        <groupId>com.github.wangzihaogithub</groupId>
        <artifactId>sse-server</artifactId>
        <version>1.2.13</version>
    </dependency>

### 包介绍
* /demo1-single 包演示了单体应用的使用方式，启动Demo1SingleApplication

访问http://127.0.0.1:8080
可以看到这样的消息，您可以进行交互测试

    ID:184320 connect-finish {"connectionId":"184320","serverTime":1689412819469,"reconnectTime":5000,"name":"defaultConnectionService","enableCluster":false,"version":"1.2.13"} :
    ID:184320 server-push {"name":"sendByUserId 服务端推送的"} :
    ID:184320 server-push {"name":"sendByUserId 服务端推送的"} :
    ID:184320 connect-close {"connectionId": "184320"}. whoTriggerClose=client :
    ID:847140540416 connect-finish {"connectionId":"847140540416","serverTime":1689413021434,"reconnectTime":5000,"name":"defaultConnectionService","enableCluster":false,"version":"1.2.13"} :
    ID:847140540416 server-push {"name":"sendByUserId 服务端推送的"} :
    ID:847140540416 server-push {"name":"sendByUserId 服务端推送的"} :


* /demo2-distributed 包演示了集群多实例应用的使用方式，启动以下4个服务，
    Demo2dDistributedDubbo2Application
    Demo2dDistributedDubbo1Application
    Demo2dDistributedTomcat1Application
    Demo2dDistributedTomcat2Application

访问http://127.0.0.1:8080，http://127.0.0.1:8081
可以看到这样的消息，您可以进行交互测试

    ID:21468848128 connect-finish {"connectionId":"21468848128","serverTime":1689413086483,"reconnectTime":5000,"name":"defaultConnectionService","enableCluster":true,"version":"1.2.13"} :
    ID:21468848128 server-push {"name":"sendByUserId dubbo2号推送的"} :
    ID:21468848128 server-push {"name":"sendByUserId dubbo1号推送的"} :
    ID:21468848128 server-push {"name":"sendByUserId dubbo2号推送的"} :
    ID:21468848128 server-push {"name":"sendByUserId dubbo1号推送的"} :

### 项目原理

1. 前端浏览器通过 

       import(后段接口).then(conn => {
           实现获得sse监听对象
       }) 

2. 后端服务端，直接面向前端，提供http接口

通过继承 SseWebController<MyAccessUser> 实现提供http-sse接口

        @RestController
        @RequestMapping("/my-sse")
        public class MySseWebController extends SseWebController<MyAccessUser> 

获取sse链接方式如下

        @Resource
        private LocalConnectionService localConnectionService; // 通过这种方式获得sse链接
   
        注！如果一个应用需要报漏多个SseWebController服务，请手工向spring注册LocalConnectionService


3. 集群需要用户添加依赖nacos， 开启配置
    
        spring:
            sse-server:
                remote:
                    enabled: true
                        nacos:
                            service-name: 'demo2-distributed'
                            server-addr: 'xx.xx.xx.xx'

4. 开启集群后，支持后端客户端（不依赖tomcat）

         @Resource
         private DistributedConnectionService distributedConnectionService; // 这种方式获得sse链接

5. qos发送，保证至少发送一次

         // 可以通过下面两个接口发送
         localConnectionService.qos().sendByUserId()
         distributedConnectionService.qos().sendByUserId()
    
### QA问答

    Q：开启集群后的如何确认消息落点并转发，怎么实现的？
    A: sse-client（DistributedConnectionService），sse-controller(SseWebController), localConnectionService.getCluster()
       这三个发消息入口发消息前会先看本地是否存在该链接ID，如果不存在则广播集群所有机器，
       每个sse-server内置暴露了随机端口号的http接口，并注册到nacos上（放心有安全机制）

    Q：如何保证qos？
    A：如果用户不在线，会将消息存放在MessageRepository接口中，如果下次用户上线，会触发重新发送并删除消息，
       目前MessageRepository的实现是MemoryMessageRepository，未来会增加其他实现
