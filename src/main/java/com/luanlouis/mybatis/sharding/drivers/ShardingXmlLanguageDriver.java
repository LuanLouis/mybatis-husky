package com.luanlouis.mybatis.sharding.drivers;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;
import com.luanlouis.mybatis.sharding.Constants;
import com.luanlouis.mybatis.sharding.strategy.DataSourceBasedShardingTableStrategy;
import com.luanlouis.mybatis.sharding.strategy.ShardingTableStrategy;
import com.luanlouis.spring.boot.SpringContext;
import javafx.application.Application;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.OgnlCache;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.luanlouis.mybatis.sharding.Constants.DEFAULT_IDENTIFIER_KEY;
import static com.luanlouis.mybatis.sharding.Constants.MyBatisVariables.*;

/**
 * Sharding based XmlLanguage Driver
 *
 * @author louluan@hotmail.com
 * @date 2019/03/08
 */

@Component("shardingXmlLanguageDriver")
public class ShardingXmlLanguageDriver extends XMLLanguageDriver {

    private static final Logger log = LoggerFactory.getLogger(ShardingXmlLanguageDriver.class);

    private ShardingTableStrategy routingTableStrategy = new ShardingTableStrategy.Default();

    private Configuration configuration;

    private String shardingIdentifierKey;

    /**
     * jdbc provider
     */
    private String shardingJdbcType;

    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
        if (initializedIfRequired(configuration)) {
            return new RoutingSqlSource(sqlSource);
        }
        return sqlSource;
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
        if (initializedIfRequired(configuration)) {
            return new RoutingSqlSource(sqlSource);
        }
        return sqlSource;
    }

    protected boolean initializedIfRequired(Configuration configuration) {
        if (null == this.configuration) {
            synchronized (this) {
                this.configuration = configuration;

                String identifierKey = this.configuration.getVariables().getProperty(SHARDING_IDENTIFIER_KEY);
                if (null == identifierKey || "".equals(identifierKey)) {
                    shardingIdentifierKey = DEFAULT_IDENTIFIER_KEY;
                } else {
                    shardingIdentifierKey = identifierKey;
                }
                String sourceType = this.configuration.getVariables().getProperty(SHARDING_CONFIG_SOURCE);
                String dataSourceKey = this.configuration.getVariables().getProperty(SHARDING_CONFIG_DATASOURCE);

                shardingJdbcType = this.configuration.getVariables().getProperty(SHARDING_DATASOURCE_TYPE, JdbcConstants.MYSQL).toLowerCase();

                if (Constants.ConfigSource.DATASOURCE.equals(sourceType)) {
                    DataSource dataSource;
                    try {
                        dataSource = SpringContext.getAppContext().getBean(dataSourceKey, DataSource.class);
                    } catch (BeansException beansException) {
                        log.error("MyBatis Husky component initialized failed due to  dataSource {} is not found in Spring context", dataSourceKey);
                        log.info("try load primary datasource from spring context...");
                        try {
                            dataSource = SpringContext.getAppContext().getBean(DataSource.class);
                        } catch (BeansException primaryException) {
                            log.error("MyBatis Husky component initialized failed due to primary dataSource is not found in Spring context");
                            throw primaryException;
                        }
                    }
                    routingTableStrategy = new DataSourceBasedShardingTableStrategy(dataSource);
                }

            }
        }
        return true;
    }

    class RoutingSqlSource implements SqlSource {

        private SqlSource delegate;

        RoutingSqlSource(SqlSource delegate) {
            this.delegate = delegate;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {

            BoundSql result = delegate.getBoundSql(parameterObject);
            String identifier = "";
//            Optional<ParameterMapping> parameterMapping = result.getParameterMappings().stream().filter(item -> shardingIdentifierKey.equals(item.getProperty())).findFirst();
//            if (!parameterMapping.isPresent()) {
//                result.getParameterObject().stream().filter(item -> shardingIdentifierKey.equals(item.getProperty())).findFirst();
//                return result;
//            }
            try {
                final Map value = (Map) OgnlCache.getValue("#{" + shardingIdentifierKey + "}", result.getParameterObject());
                Object parameterValue = value.keySet().toArray()[0];
                if (null == parameterValue) {
                    return result;
                } else {
                    identifier = String.valueOf(value.keySet().toArray()[0]);
                }
            } catch (Exception e) {
                log.error("extracting sharding identifier failed!", e);
                return result;
            }

            log.debug("Sharding condition has detected as:{}", identifier);
            List<SQLStatement> stmtList = SQLUtils.parseStatements(result.getSql(), shardingJdbcType);
            SQLStatement stmt = stmtList.get(0);
            StringBuilder stringBuilder = new StringBuilder();
            SQLASTOutputVisitor visitor = new TableRoutingMySqlAdaptor(stringBuilder, identifier);
            stmt.accept(visitor);
            return new RoutingBoundSql(result, stringBuilder.toString());
        }
    }


    /**
     * Extended BoundSql to support sharding transformation according to custom strategy
     */
    class RoutingBoundSql extends BoundSql {

        private BoundSql delegate;

        private String replacedSql;

        private String tenantId;


        RoutingBoundSql(BoundSql delegate, String replacedSql) {
            super(configuration, null, delegate.getParameterMappings(), delegate.getParameterObject());
            this.delegate = delegate;
            this.replacedSql = replacedSql;
        }

        @Override
        public String getSql() {
            String originalSql = delegate.getSql();
            log.debug("ORIGINAL SQL ==>：{}", originalSql);
            log.debug("SHARDED  SQL <==：{}", replacedSql);
            return replacedSql;
        }

        @Override
        public List<ParameterMapping> getParameterMappings() {
            return delegate.getParameterMappings();
        }

        @Override
        public Object getParameterObject() {
            return delegate.getParameterObject();
        }

        @Override
        public boolean hasAdditionalParameter(String name) {
            return delegate.hasAdditionalParameter(name);
        }

        @Override
        public void setAdditionalParameter(String name, Object value) {
            delegate.setAdditionalParameter(name, value);
        }

        @Override
        public Object getAdditionalParameter(String name) {
            return delegate.getAdditionalParameter(name);
        }
    }

    /**
     * druid visitor to override  table name when output the SQL statements
     */
    class TableRoutingMySqlAdaptor extends MySqlOutputVisitor {

        private String tenantId;

        TableRoutingMySqlAdaptor(Appendable appender, String tenantId) {
            super(appender);
            this.tenantId = tenantId;
        }

        @Override
        protected void printTableSource(SQLTableSource x) {
            Class<?> clazz = x.getClass();
            if (clazz == SQLJoinTableSource.class) {
                visit((SQLJoinTableSource) x);
            } else if (clazz == SQLExprTableSource.class) {
                printSQLExprTableSource(x);
            } else if (clazz == SQLSubqueryTableSource.class) {
                visit((SQLSubqueryTableSource) x);
            } else {
                x.accept(this);
            }
        }

        void printSQLExprTableSource(SQLTableSource tableSource) {
            if (tableSource.getClass() == SQLExprTableSource.class) {
                SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) tableSource;
                String oldTableName = sqlExprTableSource.getName().getSimpleName();
                String newTableName = routingTableStrategy.route(oldTableName, tenantId);
                if (StringUtils.isEmpty(tableSource.getAlias())) {
                    print0(newTableName);
                } else {
                    print0(newTableName + " as " + tableSource.getAlias());
                }
            }
        }
    }
}
