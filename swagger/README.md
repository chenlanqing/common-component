SpringBoot中接入Swagger
---

### 接入步骤

- 引入pom文件：

    ```xml
    <!-- swagger -->
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>2.8.0</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>2.8.0</version>
    </dependency>
    ```
    
- 增加配置文件：`com.blue.fish.swagger.config.SwaggerConfig`
- 基本用法：

    @Api : 用在类上，说明该类的主要作用。
    
    @ApiOperation：用在方法上，给API增加方法说明。
    
    @ApiImplicitParams : 用在方法上，包含一组参数说明。
    
    @ApiImplicitParam：用来注解来给方法入参增加说明


访问地址：http://localhost:8080/swagger/swagger-ui.html

如果没有上下文：http://localhost:8080/swagger-ui.html

### 界面美化

可以引入新的饿jar包：
```xml
<!--   swagger界面ui，其访问地址时doc.html     -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>swagger-bootstrap-ui</artifactId>
    <version>1.6</version>
</dependency>
```
其访问地址时：http://localhost:8080/doc.html