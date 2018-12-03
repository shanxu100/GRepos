package com.example.boottest.demo.utils.mongodb.template;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by DJH on 2018/10/22
 * 等效于spring-mongo.xml
 */

@Configuration
public class PMCMongoConfig {

    /**
     * 配置资源配置器
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("mongodb.properties"));
        return configurer;
    }

    @Bean
    public MongoClientFactoryBean mongo(
            @Value("${mongodb.host}") String host,
            @Value("${mongodb.port}") Integer port,
            @Value("${mongodb.username}") String username,
            @Value("${mongodb.password}") String password,
            @Value("${mongodb.database}") String database,
            MongoClientOptions mongoClientOptions) {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
        mongo.setHost(host);
        mongo.setPort(port);
        mongo.setMongoClientOptions(mongoClientOptions);
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        mongo.setCredentials(new MongoCredential[]{credential});
        return mongo;
    }

    @Bean
    public MongoClientOptions mongoClientOptions(
            @Value("${mongodb.maxWaitTime}") Integer maxWaitTime,
            @Value("${mongodb.connectTimeout}") Integer connectTimeout,
            @Value("${mongodb.maxConnectionIdleTime}") Integer maxConnectionIdleTime) {
        return MongoClientOptions.builder().maxWaitTime(maxWaitTime)
                .connectTimeout(connectTimeout)
                .maxConnectionIdleTime(maxConnectionIdleTime)
                .build();
    }

    @Bean
    public MongoTemplate mongoTemplate(@Value("${mongodb.database}") String database, MongoClient mongo) {
        return new MongoTemplate(mongo, database);
    }

}
