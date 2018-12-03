# 1、 取消SpringBoot自动配置MongoDB

或者 提示错误 ：

**com.mongodb.MongoSocketOpenException: Exception opening socket**

**原因**：

在创建项目的时候勾选了MongoDB相关的选项，使SpringBoot自动配置MongoDB。但是缺乏相关的配置信息，导致启动失败。

**解决方法**：

1. **增加注解**
	
	 @SpringBootApplication(exclude = MongoAutoConfiguration.class)
 
2. **删除pom.xml文件中MongoDB启动语句**

```xml

	<dependency>

            <groupId>org.springframework.boot</groupId>

            <artifactId>spring-boot-starter-data-mongodb</artifactId>

	</dependency>
```


# 2、 SpringBoot自动配置MongoDB

在创建项目时，如果勾选了MongoDB相关的选项，SpringBoot就会自动配置支持MongoDB。在启动SpringBoot时会自动实例化一个mongo实例。

1. **增加配置**

	**方式一：**

```

	spring.data.mongodb.host=xxxxxxxxx
	
	spring.data.mongodb.port=xxxx
	
	spring.data.mongodb.user=luluteam
	
	spring.data.mongodb.password=luluteam
	
	spring.data.mongodb.database=readingData 

```

	**方式二：**

```	
	格式：mongodb://用户名:密码@IP:Port/数据库名称
	
	spring.data.mongodb.uri=mongodb://luluteam:luluteam@121.199.23.184:52914/PMC
```

2. **获取实例**

```java

    @Autowired 
    private MongoTemplate mongoTemplate;

```

MongoTemplate操作数据库

spring-data-mongo提供了MongoTemplate来操作bean对象与MongoDB交互，使用的关键是如何创建一个MongoTemplate。

springboot推荐使用用java代码的形式申明注册bean。 
@Configuration注解可以用java代码的形式实现spring中xml配置文件配置的效果。具体例子可参考 template文件夹下PMCMongoConfig.java 例子

3. 增删改查