package com.luanlouis.mybatis.husky;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author terrylouis1991@gmail.com
 * @date 2019/04/09
 */
@EnableAutoConfiguration
@Configuration
public class TestBootApplication {

    @Bean
    public DataSource before(){
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql") ///启动时初始化建表语句
                .addScript("classpath:data.sql")
                .build();
    }

}
