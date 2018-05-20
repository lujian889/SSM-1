                                                                                             
 

使用Idea搭建的Maven项目，会不定期更新一些在实际开发中使用的技巧,没有复杂的业务流程，更不是**XXXX系统**，只有一些技术的分享

## 目录结构

```shell
├── SSM-API                                    // 通用API  
│   ├── src/main
│   ├── ├──java/com/crossoverJie               // 具体代码。  
│   ├── ├──resources
├── SSM-BOOT                                   // 提供给内部使用的dubbo依赖
│   ├── ├──resources/spring                    // dubbo consumer相关配置
├── SSM-SERVICE                                // dubbo应用的service实现
│   ├── src/main
│   ├── ├──java/com/crossoverJie/api           // 具体代码
│   ├── ├──├──controller                       // 心跳检测接口
│   ├── ├──├──dubbo                            // dubbo相关代码
│   ├── ├──├──├──dao                           // dao层
│   ├── ├──├──├──pojo                          // pojo包
│   ├── ├──├──├──service                       // service层
│   ├── ├──├──├──util                          // 工具类
│   ├── ├──├──impl                             // dubbo API实现
│   ├── ├──resources                           // 配置文件
│   ├── ├──├──mapping                          // *.mapper配置文件
│   ├── ├──├──spring                           // spring相关配置文件
├── SSM-WEB                                    // web应用
│   ├── src/main
│   ├── ├──java/com/crossoverJie               // 具体代码
│   ├── ├──├──controller                       // 控制器层
│   ├── ├──├──cxf                              // CXF相关代码
│   ├── ├──├──dao                              // dao层
│   ├── ├──├──enums                            // 枚举包
│   ├── ├──├──intercept                        // 拦截器
│   ├── ├──├──kafka                            // Kafka
│   ├── ├──├──├──official                      // 官方消费
│   ├── ├──├──├──optimization                  // 多线程消费
│   ├── ├──├──lucene                           // Lucene 相关包
│   ├── ├──├──pojo                             // pojo包
│   ├── ├──├──req                              // 请求类
│   ├── ├──├──res                              // 响应类
│   ├── ├──├──service                          // service层
│   ├── ├──├──shiro                            // shiro相关代码
│   ├── ├──├──util                             // 工具类
│   ├── ├──├──vo                               // vo包
│   ├── ├──resources
│   ├── ├──├──mapping                          // *.mapper配置文件
│   ├── ├──webapp                              // 前端文件
├── doc
│   ├──lucene                                  // lucene文件
│   ├──sql                                     // sql脚本
├── .gitignore                                 // git忽略项
├── pom.xml                                    // 父pom
├── LICENSE               
├── README.md               

```

 


