package com.luanlouis.jdbc.instropect;

import com.alibaba.druid.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author louluan(of2256)
 * @date 2019/04/15
 */
public class TableMetaData {


    private String tableName;

    private String comments;

    private String charset = "utf8mb4";

    private List<TableColumnMetaData> columnMetaDataList;


    private List<IndexMetaData> indexMetaDataList;

    public TableMetaData(String tableName, String comments, List<TableColumnMetaData> columnMetaDataList, List<IndexMetaData> indexMetaDataList) {
        this.tableName = tableName;
        this.comments = comments;
        this.columnMetaDataList = columnMetaDataList;
        this.indexMetaDataList = indexMetaDataList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<TableColumnMetaData> getColumnMetaDataList() {
        return columnMetaDataList;
    }

    public void setColumnMetaDataList(List<TableColumnMetaData> columnMetaDataList) {
        this.columnMetaDataList = columnMetaDataList;
    }

    public List<IndexMetaData> getIndexMetaDataList() {
        return indexMetaDataList;
    }

    public void setIndexMetaDataList(List<IndexMetaData> indexMetaDataList) {
        this.indexMetaDataList = indexMetaDataList;
    }

    /**
     * 获取创建table sql 语句
     * @return
     */
    public String getCreateTableSql(){
       return copyTableSql(this.tableName);
    }

    public String copyTableSql(String tableName){
        StringBuilder sql = new StringBuilder();
        String columns = String.join(",\n",columnMetaDataList.stream().map(item-> "\t"+item.asString()).collect(Collectors.toList()));
        sql.append(" CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append("( \n")
                .append(columns)
                .append("( \n")
                .append(") ")
                .append("ENGINE=InnoDB DEFAULT CHARSET=")
                .append(charset)
                .append("\n");
        if(!StringUtils.isEmpty(comments)){
            sql.append("COMMENT ").append("'").append(comments).append("'\n");
        }
        sql.append(";");
        return sql.toString();
    }

    /**
     * 获取创建该索引的语句
     *
     * @return
     */
    public List<String> getIndexSql() {
        return this.getIndexSql(this.tableName);
    }

    /**
     * 获取创建该索引的语句
     *
     * @return
     */
    public List<String> getIndexSql(String tableName) {
        Map<String, List<IndexMetaData>> groupedIndexData = indexMetaDataList.stream().collect(Collectors.groupingBy(IndexMetaData::getINDEX_NAME));
        List<String> indexSql = new ArrayList<>();
        for (Map.Entry<String, List<IndexMetaData>> entry : groupedIndexData.entrySet()) {
            StringBuilder indexBuffer = new StringBuilder();
            entry.getValue().sort((Comparator.comparingInt(IndexMetaData::getORDINAL_POSITION)));
            String columns = String.join(",", entry.getValue().stream().map(IndexMetaData::getCOLUMN_NAME).collect(Collectors.toList()));
            indexBuffer.append("CREATE ");
            if (!entry.getValue().get(0).isNON_UNIQUE()) {
                indexBuffer.append("UNIQUE");
            }
            indexBuffer.append(" INDEX ")
                    .append(entry.getKey())
                    .append(" ON ")
                    .append(tableName)
                    .append("(")

                    .append(columns)
                    .append(")");
            indexSql.add(indexBuffer.toString());
        }
        return indexSql;
    }


}
