
 

 
 
I'll update some skills in actual development aperiodically.There are no complicated operation flows or **xxx system**,just some practical shares about skills.

## Directory Structure

```shell
├── SSM-API                                    // common API  
│   ├── src/main
│   ├── ├──java/com/crossoverJie               // specific code。  
│   ├── ├──resources
├── SSM-BOOT                                   // Available for internal use of dubbo dependencies 
│   ├── ├──resources/spring                    // dubbo consumer configuration
├── SSM-SERVICE                                // The service implementation of the dubbo application 
│   ├── src/main
│   ├── ├──java/com/crossoverJie/api           // specific code
│   ├── ├──├──controller                       // Heartbeat detection interface
│   ├── ├──├──dubbo                            // Dubbo related code
│   ├── ├──├──├──dao                           // dao package
│   ├── ├──├──├──pojo                          // pojo package
│   ├── ├──├──├──service                       // service package
│   ├── ├──├──├──util                          // Toolkit
│   ├── ├──├──impl                             // implement dubbo API
│   ├── ├──resources                           // configuration file
│   ├── ├──├──mapping                          // *.mapper configuration file
│   ├── ├──├──spring                           // Spring related configuration file
├── SSM-WEB                                    // web application
│   ├── src/main
│   ├── ├──java/com/crossoverJie               // specific code
│   ├── ├──├──controller                       // controller package
│   ├── ├──├──cxf                              // CXF related code
│   ├── ├──├──dao                              // dao package
│   ├── ├──├──enums                            // enum package
│   ├── ├──├──intercept                        // Interceptor
│   ├── ├──├──kafka                            // Kafka
│   ├── ├──├──├──official                      // Official consumption
│   ├── ├──├──├──optimization                  // Multi-threaded consumption
│   ├── ├──├──lucene                           // Lucene related code
│   ├── ├──├──pojo                             // pojo package
│   ├── ├──├──req                              // request package
│   ├── ├──├──res                              // response package
│   ├── ├──├──service                          // service pachage
│   ├── ├──├──shiro                            // shiro related code
│   ├── ├──├──util                             // Toolkit
│   ├── ├──├──vo                               // vo package
│   ├── ├──resources
│   ├── ├──├──mapping                          // *.mapper configuration file
│   ├── ├──webapp                              // front code
├── doc
│   ├──lucene                                  // lucene related code
│   ├──sql                                     // sql scripts
├── .gitignore                                 // gitignore 
├── pom.xml                                    // parent pom
├── LICENSE               
├── README.md               

```
 
# Contact Author
- [172257861@qq.com](mailto:172257861@qq.com)

 
