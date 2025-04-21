# 简易的RPC框架

## 项目介绍

基于 Java + Etcd + Vert.x 的 RPC 框架, 实现了 SPI 机制加载自定义配置, 基于 Vert.x 开发了 Tcp 和 Http 两种客户端访问模式, 实现多种序列化器, 以及基于 Etcd 实现的注册中心, 多种负载均衡器, 自定义网络协议, 服务的重试容错机制, Mock机制等等. 项目完成了 springboot-starter, 可以通过注解实现服务的注册与服务注入



**技术选型**

- ⭐️ Vert.x 框架
- ⭐️ Etcd 云原生存储中间件（jetcd 客户端）
- ⭐️ SPI 机制
- ⭐️ 多种序列化器
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



## 使用方法

安装ETCD注册中心

官方下载页：https://github.com/etcd-io/etcd/releases

可视化工具etcdkeeper：️https://github.com/evildecay/etcdkeeper/

etcdkeeper启动命令`etcdkeeper -p 9090`



## 项目完整配置

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



### 消费者简单配置

```yaml
rpc:
  registry:
    # 注册中心地址
    address: http://localhost:2379
    # 注册中心调用超时时间
    timeout: 5000
```

### 服务者简单配置

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
    # 注册中心调用超时时间
    timeout: 5000
```

