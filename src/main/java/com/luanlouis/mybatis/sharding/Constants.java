package com.luanlouis.mybatis.sharding;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author terrylouis1991@gmail.com
 * @date 2019/03/28
 */
public class Constants {

    /**
     * default identifier key used by mybatis parameters
     */
    public static final String DEFAULT_IDENTIFIER_KEY = "_identifier";

    public static final class MyBatisVariables {

        /**
         * sharding identifier key
         */
        public static final String SHARDING_IDENTIFIER_KEY = "_SHARDING_IDENTIFIER_KEY";

        /**
         * which datasource should be used to query sharding config rule
         */
        public static final String SHARDING_CONFIG_DATASOURCE = "_SHARDING_CONFIG_DATASOURCE";

        /**
         * DEFAULT:
         *     simple concatenation as oldTableName + _ + identifier
         * DATASOURCE:
         *     load configuration from datasource
         *
         */
        public static final String SHARDING_CONFIG_SOURCE = "_SHARDING_CONFIG_SOURCE";


        /**
         * JDBC type
         *  可填：
         *  mysql
         *  oracle
         *  maria
         *
         *
         */
        public static final String SHARDING_DATASOURCE_TYPE = "_SHARDING_DATASOURCE_TYPE";

    }

    public static final class ConfigSource{
        public static final String DEFAULT = "DEFAULT";
        public static final String DATASOURCE = "DATASOURCE";
    }

    /**
     * 分片方式
     */
    public static final class ShardingRule{

        /**
         * 静态匹配
         */
        public static final int STATIC = 0;

        /**
         * 表达式动态匹配
         */
        public static final int DYNAMIC = 1;

        /**
         * 自定义实现
         */
        public static final int CUSTOMIZED = 2;

    }


}
