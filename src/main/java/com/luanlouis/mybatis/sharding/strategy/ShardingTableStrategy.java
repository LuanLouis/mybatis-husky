package com.luanlouis.mybatis.sharding.strategy;

/**
 *
 *  Sharding table strategy definition
 *
 * @author terrylouis1991@gmail.com
 * @date 2019/03/08
 */
public interface ShardingTableStrategy {


    /**
     *
     *  According to old table name and identifier to decide the transformed table name
     *
     * @param oldTableName  old table name
     * @param identifier    identifier value
     * @return transformed table name
     */
    String route(String oldTableName, String identifier);



    class Default implements ShardingTableStrategy {

        /**
         *
         *  According to old table name and identifier to decide the transformed table name
         *   default implementation
         *
         * @param oldTableName  old table name
         * @param identifier    identifier value
         * @return transformed table name
         */
        @Override
        public String route(String oldTableName, String identifier) {
            return oldTableName+"_"+ identifier;
        }
    }

}
