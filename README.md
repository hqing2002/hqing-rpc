# HqRPC - 轻量级RPC框架

![Java Version](https://img.shields.io/badge/Java-11%252B-blue.svg) ![Vert.x Version](https://img.shields.io/badge/Vert.x-4.5.13-green.svg) ![License](https://img.shields.io/badge/License-Apache%25202.0-red.svg) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.6.13-green?logo=spring-boot&logoColor=white&color=6DB33F) ![etcd](https://img.shields.io/badge/etcd-3.6.0-blue?logo=etcd&logoColor=white&color=419EDA) ![Hutool](https://img.shields.io/badge/Hutool-5.8.24-orange?logo=java&logoColor=white&color=FF6F00) ![snakeYAML](https://img.shields.io/badge/snakeYAML-2.2-purple?logo=snakeyaml&logoColor=white&color=6C3483) ![Guava](https://img.shields.io/badge/Guava-2.0.0-yellow?logo=google&logoColor=white&color=F4B400)

## 🚀项目介绍

HqRpc是一个基于 Java + Etcd + Vert.x 的轻量级 RPC (远程服务调用)框架，简化了远程服务的调用过程。


## 🌟核心特性

- **⚙️配置加载：** 支持多种配置文件格式，使用 SPI 机制根据配置文件参数动态加载框架接口实现。
- **🛠️注解开发：** 引入 rpc-springboot-starter 可以使用注解开发，让框架使用更加简单。
- **🔗注册中心：** 使用 ETCD 构建服务注册中心，支持服务心跳检测。
- **💾路由缓存：** 服务消费端支持路由缓存，利用 ETCD 的节点监听完成路由缓存实时更新。
- **📦自定义RPC协议：** TCP 服务器采用自定义协议，开发了消息编码器和消息解码器对协议消息进行处理。
- **🔌服务暴露：** 使用 Vert.X 构建服务器进行服务暴露，支持切换服务器类型 TCP 和 HTTP。
- **🔖版本控制：** 服务注册和远程调用支持服务版本号，消费端可切换服务版本号完成服务版本控制。
- **🔐序列化：** 支持多种序列化器配置，支持使用者自定义序列化器扩展。
- **⚖️负载均衡：** 支持多种负载均衡策略，轮询、随机、一致性哈希。
- **⏳重试：** 支持多种重试策略，不重试、随机事件间隔、指数时间间隔等，支持使用者自定义重试策略。
- **🛑容错:** 支持 多种容错策略，快速失败、故障转移等，支持使用者自定义容错策略。
- **⏱️超时控制：** 用户可以通过配置文件设置 RPC 远程调用超时时长。
- **🧪模拟调用：** 提供MOCK支持，服务消费端配置 mock 后无需服务提供者即可生成测试数据。

## 📁项目结构

```
hqing-rpc/
│
├── example-common / # 示例公共模块
│
├── example-consumer/ # 示例消费者模块
│
├── example-provider/ # 示例服务提供者模块
│
├── example-provider-1/ # 示例服务提供者模块1
│
├── example-provider-2/ # 示例服务提供者模块2
│
├── example-springboot-consumer/ # 服务消费者注解开发版本
│
├── example-springboot-provider/ # 服务提供者注解开发版本
│
├── hqing-rpc-core/ # RPC框架核心实现
│
└── hqing-rpc-spring-boot-starter/ # SpringBootStarter 项目启动器
```

## 🌱快速开始

### 1、创建公共模块

在公共模块中创建需要传输的对象和远程调用接口

```java
public class User {
    private String name;

    public User(String name) {
        this.name = name;
    }
}

public interface UserService {
    User getUser();
}
```

### 2、导入依赖项

在服务消费者和服务提供者模块中都引入公共模块和 RpcStarter

```xml
<dependency>
    <groupId>com.hqing</groupId>
    <artifactId>hqing-rpc-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.hqing</groupId>
    <artifactId>example-common</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 3、配置服务提供者

编写application.yml

```yaml
rpc:
  # 协议配置
  protocol:
    # 服务暴露端口(服务提供者配置此参数)
    port: 28800
  # 注册中心配置
  registry:
    # 注册中心地址
    address: http://localhost:2379
```

在启动类上使用`@EnableRpc`注解来启用 RPC 服务功能。参数说明：

- needServer: 是否需要开启服务暴露，默认True
- basePackages：指定扫描包路径，默认启动类包名

```java
//开启RPC服务
@EnableRpc
@SpringBootApplication
public class ExampleSpringbootProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootProviderApplication.class, args);
    }
}
```

服务提供者编写接口实现，使用注解`@RpcService`标记为需要暴露的服务实现，参数说明：

- interfaceClass：指定该服务实现了哪个接口，默认该类实现的第一个接口
- serviceVersion：服务版本号，默认1.0

```java
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public User getUser() {
        return new User("RPC");
    }
}
```

### 3、配置服务消费者

编写application.yml

```yaml
rpc:
  registry:
    # 注册中心地址
    address: http://localhost:2379
```

在启动类上使用`@EnableRpc`注解来启用 RPC 服务功能。将 needServer 设置为 False

```java
@EnableRpc(needServer = false)
@SpringBootApplication
public class ExampleSpringbootConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootConsumerApplication.class, args);
    }
}
```

编写远程调用，使用注解`@RpcReference`注入代理对象，参数说明：

- interfaceClass：指定该服务需要哪个接口的实现类，默认为字段类型
- serviceVersion：指定需要哪个版本的服务实现，默认1.0

```java
@Service
public class ExampleServer {
    @RpcReference
    private UserService userService;

    public void testRpc() {
        User user = userService.getUser();
        if (user == null) {
            System.out.println("远程调用失败");
            return;
        }
        System.out.println(user);
    }
}
```

编写单元测试

```java
@SpringBootTest
class ExampleSpringbootConsumerApplicationTests {
    @Resource
    private ExampleServer exampleServer;

    @Test
    void contextLoads() {
        exampleServer.testRpc();
    }
}
```

## ⚙️项目完整配置

```yaml
rpc:
  # 协议配置
  protocol:
    # 协议类型(tcp/http)服务端和消费端需保持一致
    name: tcp
    # 服务暴露端口(服务提供者配置此参数)
    port: 28800
    # 序列化器(jdk/json/kryo/hessian/customize)用户自定义序列化器需要使用键customize
    serializer: jdk
  # 注册中心配置
  registry:
    # 注册中心名称(etcd)
    name: etcd
    # 注册中心地址
    address: http://localhost:2379
    # 注册中心用户名
    username: root
    # 注册中心密码
    password: root
    # 注册中心调用超时时间
    timeout: 5000
  # 消费者相关配置
  consumer:
    # 开启mock调用(服务消费端测试使用)
    mock: true
    # 远程调用超时时间
    timeout: 5000
    # 负载均衡策略(roundRobin/random/consistentHash)
    loadBalancer: roundRobin
    # 重试策略(noRetry/fixedInterval/exponentialInterval/incrementingInterval/randomInterval)
    retryStrategy: noRetry
    # 容错策略(failFast/failSafe/failOver)
    tolerantStrategy: failFast
```



**消费者最简配置**

```yaml
rpc:
  registry:
    # 注册中心地址
    address: http://localhost:2379
```



**服务者最简配置**

```yaml
rpc:
  # 协议配置
  protocol:
    # 服务暴露端口(服务提供者配置此参数)
    port: 28800
  # 注册中心配置
  registry:
    # 注册中心地址
    address: http://localhost:2379
```



## ✨技术选型

- ⭐️ Vert.x 框架
- ⭐️ Etcd 云原生存储中间件（jetcd 客户端）
- ⭐️ SPI 机制
- ⭐️ 多种序列化器
  - JDK 序列化
  - JSON 序列化
  - Kryo 序列化
  - Hessian 序列化

- ⭐️ 多种设计模式
  - 双检锁单例模式
  - 工厂模式
  - 代理模式
  - 装饰者模式
- ⭐️ Spring Boot Starter 开发
- 反射和注解驱动
- Guava Retrying 重试库
- JUnit 单元测试
- Logback 日志库
- Hutool、Lombok 工具库
