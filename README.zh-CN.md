## MyBatis - Husky
**MyBatis Husky** is  a lightweight and efficient table sharding middleware implemented by extending MyBatis `LanguageDriver`
(基于MyBatis LanguageDriver 拓展实现的轻量级高效的水平分表中间件) 

#### What does it do ?





#### Version
```xml
<dependency>
    <groupId>com.luanlouis</groupId>
    <artifactId>mybatis-husky</artifactId>
    <version>*latest-version*</version>
</dependency>
```

#### Dependency
```xml



```

#### Configuration
Step1. Add dependency into your pom.xml
```java



```

Step2. Customize MyBatis configurations
```properties
## set customized ShardingXmlLanguageDriver to override the default one
mybatis.configuration.default-scripting-language=com.luanlouis.mybatis.sharding.drivers.ShardingXmlLanguageDriver
## sharding identifier that can be used to extract from parameter Object
mybatis.configuration.variables._SHARDING_IDENTIFIER_KEY=tenantId
## where to load sharding rule configuration
mybatis.configuration.variables._SHARDING_CONFIG_SOURCE=DATASOURCE
## if above source is configured as `DATASOURCE`,then this value should be configured the bean id in spring context
mybatis.configuration.variables._SHARDING_CONFIG_DATASOURCE=primaryDataSource
```




