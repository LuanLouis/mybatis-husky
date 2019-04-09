package com.luanlouis.mybatis.sharding.drivers;

import com.luanlouis.mybatis.sharding.strategy.ShardingTableStrategy;

import java.util.StringJoiner;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author terrylouis1991@gmail.com
 * @date 2019/03/28
 */
public class ShardingRule implements Comparable<ShardingRule>{

    /**
     * 主键ID
     */
    private Integer shardingId;

    /**
     * 分表描述
     */
    private String shardingName;

    /**
     * 数据库表名
     */
    private String tableName;

    /**
     * 分表策略
     * 替换策略，0：表达式匹配，返回静态分表值；1：表达式匹配，返回动态分表值，这种方式分表参数作为POSTFIX;2:完全自定义实现
     */
    private Integer strategy;

    /**
     * 匹配表达式
     */
    private String expression;
    /**
     * 分隔符, 下划线_
     */
    private String delimiter;

    /**
     * 添加后缀
     */
    private String postfix;


    /**
     * 默认实现类
     */
    private String customImpl;

    private ShardingTableStrategy customImplInstance;

    /**
     * 优先级顺序，数字越大，优先级有恶搞
     */
    private Integer precedence;


    public Integer getShardingId() {
        return shardingId;
    }

    public void setShardingId(Integer shardingId) {
        this.shardingId = shardingId;
    }

    public String getShardingName() {
        return shardingName;
    }

    public void setShardingName(String shardingName) {
        this.shardingName = shardingName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getStrategy() {
        return strategy;
    }

    public void setStrategy(Integer strategy) {
        this.strategy = strategy;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public String getCustomImpl() {
        return customImpl;
    }

    public void setCustomImpl(String customImpl) {
        this.customImpl = customImpl;
    }

    public Integer getPrecedence() {
        return precedence;
    }

    public void setPrecedence(Integer precedence) {
        this.precedence = precedence;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ShardingRule.class.getSimpleName() + "[", "]")
                .add("shardingId=" + shardingId)
                .add("shardingName='" + shardingName + "'")
                .add("tableName='" + tableName + "'")
                .add("strategy=" + strategy)
                .add("expression='" + expression + "'")
                .add("delimiter='" + delimiter + "'")
                .add("postfix='" + postfix + "'")
                .add("customImpl='" + customImpl + "'")
                .add("precedence=" + precedence)
                .toString();
    }

    @Override
    public int compareTo(ShardingRule o) {
        return this.precedence - o.getPrecedence();
    }

    public ShardingTableStrategy getCustomImplInstance() {
        return customImplInstance;
    }

    public void setCustomImplInstance(ShardingTableStrategy customImplInstance) {
        this.customImplInstance = customImplInstance;
    }
}
