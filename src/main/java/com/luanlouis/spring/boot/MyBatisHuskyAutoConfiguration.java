package com.luanlouis.spring.boot;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author terrylouis1991@gmail.com
 * @date 2019/04/08
 */
@Configuration
@AutoConfigureBefore(MybatisAutoConfiguration.class)
@ComponentScan
public class MyBatisHuskyAutoConfiguration {

}
