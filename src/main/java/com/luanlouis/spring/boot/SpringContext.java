package com.luanlouis.spring.boot;

import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 *
 * Spring Context holder and MyBatis customizer
 *
 * @author terrylouis1991@gmail.com
 * @date 2019/04/08
 */
@Component("mybatisHuskyApplicationContext")
@Order(HIGHEST_PRECEDENCE)
public class SpringContext implements ApplicationContextAware, ConfigurationCustomizer {

    static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getAppContext() {
        return context;
    }

    /**
     * Customize the given a {@link Configuration} object.
     *
     * @param configuration the configuration object to customize
     */
    @Override
    public void customize(Configuration configuration) {
    }
}
