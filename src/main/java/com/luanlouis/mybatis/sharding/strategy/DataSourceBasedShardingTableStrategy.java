package com.luanlouis.mybatis.sharding.strategy;

import com.luanlouis.mybatis.sharding.Constants;
import com.luanlouis.mybatis.sharding.drivers.ShardingRule;
import com.luanlouis.spring.boot.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.luanlouis.mybatis.sharding.Constants.ShardingRule.CUSTOMIZED;
import static com.luanlouis.mybatis.sharding.Constants.ShardingRule.DYNAMIC;
import static com.luanlouis.mybatis.sharding.Constants.ShardingRule.STATIC;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author terrylouis1991@gmail.com
 * @date 2019/03/26
 */
public class DataSourceBasedShardingTableStrategy implements ShardingTableStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceBasedShardingTableStrategy.class);
    
    /**
     * 加载配置语句
     */
    private static final String CONST_SHARDING_RULE = "SELECT SHARDING_ID,SHARDING_NAME,TABLE_NAME,STRATEGY,EXPRESSION,DELIMITER,POSTFIX,CUSTOM_IMPL,PRECEDENCE FROM t_sharding_table";

    private static final String CONST_SHARDING_TABLE = "t_sharding_table";

    private volatile Map<String, List<ShardingRule>> sortedShardingMap = new ConcurrentHashMap<>();

    private DataSource dataSource;

    public DataSourceBasedShardingTableStrategy(DataSource dataSource) {
        this.dataSource = dataSource;
        List<ShardingRule> shardingRules = new CopyOnWriteArrayList<>();
        try {
            logger.info("MyBatis husky components begin load configurations from Database {} table {}",dataSource.getConnection().getSchema(), CONST_SHARDING_TABLE);
            ResultSet resultSet = this.dataSource.getConnection().prepareStatement(CONST_SHARDING_RULE).executeQuery();
            while (resultSet.next()) {
                ShardingRule shardingRule = new ShardingRule();
                shardingRule.setShardingId(resultSet.getInt("SHARDING_ID"));
                shardingRule.setShardingName(resultSet.getString("SHARDING_NAME"));
                shardingRule.setTableName(resultSet.getString("TABLE_NAME"));
                shardingRule.setStrategy(resultSet.getInt("STRATEGY"));
                shardingRule.setExpression(resultSet.getString("EXPRESSION"));
                shardingRule.setDelimiter(resultSet.getString("DELIMITER"));
                shardingRule.setPostfix(resultSet.getString("POSTFIX"));
                shardingRule.setCustomImpl(resultSet.getString("CUSTOM_IMPL"));
                shardingRule.setPrecedence(resultSet.getInt("PRECEDENCE"));
                if (null != shardingRule.getCustomImpl() && !"".equals(shardingRule.getCustomImpl())) {
                    Class customImplClass = Class.forName(shardingRule.getCustomImpl());

                    if(!ShardingTableStrategy.class.isAssignableFrom(customImplClass)){
                        logger.error("customized sharding rule {} implementation is not sub class of ShardingTableStrategy, please check it!!!",shardingRule.getShardingName());
                        throw new IllegalArgumentException("sharding rule implementation is illegal");
                    }
                    Object instance = customImplClass.newInstance();
                    if (ApplicationContextAware.class.isAssignableFrom(customImplClass)){
                        ((ApplicationContextAware)instance).setApplicationContext(SpringContext.getAppContext());
                    }
                    shardingRule.setCustomImplInstance((ShardingTableStrategy) instance);
                }
                shardingRules.add(shardingRule);
            }
        } catch (Exception e) {
            logger.error("Exception happened during loading ShardingRule from data source, please mark sure whether table `t_sharding_table` is created correctly.", e);
            throw new RuntimeException("SHARDING_LOAD_ERROR");
        }
        Map<String, List<ShardingRule>> shardingRuleMap = shardingRules.stream().collect(Collectors.groupingBy(ShardingRule::getTableName));
        // order by
        shardingRuleMap.entrySet().forEach(stringShardingRuleEntry -> Collections.sort(stringShardingRuleEntry.getValue()));
        sortedShardingMap.putAll(shardingRuleMap);
        logger.info("sharding configuration is loaded as {}",sortedShardingMap.keySet());
    }

    /**
     *
     *  According to old table name and identifier to decide the transformed table name
     *
     * @param oldTableName  old table name
     * @param identifier    identifier value
     * @return transformed table name
     */
    @Override
    public String route(String oldTableName, String identifier) {
        List<ShardingRule> shardingRules = sortedShardingMap.get(oldTableName);
        if (null == shardingRules || shardingRules.isEmpty()) {
            return oldTableName;
        }
        Optional<ShardingRule> shardingRule = shardingRules.stream().filter(item -> identifier.matches(item.getExpression())).findFirst();
        if (!shardingRule.isPresent()) {
            return oldTableName;
        }
        ShardingRule instance = shardingRule.get();

        String newTableName = oldTableName;
        switch (instance.getStrategy()) {
            case STATIC:
                newTableName = oldTableName + instance.getDelimiter() + instance.getPostfix();
                break;
            case DYNAMIC:
                newTableName = oldTableName + "_" + identifier;
                break;
            case CUSTOMIZED:
                newTableName = instance.getCustomImplInstance().route(oldTableName, identifier);
        }
        logger.debug("table {} is sharded as new name {} using sharding rule:{}", oldTableName, newTableName, instance);
        return newTableName;
    }
}
