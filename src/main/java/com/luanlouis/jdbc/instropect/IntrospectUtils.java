package com.luanlouis.jdbc.instropect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author louluan(of2256)
 * @date 2019/04/15
 */
public class IntrospectUtils {


    public static TableMetaData introspect(String originalTableName, DataSource dataSource) throws SQLException {
        DatabaseMetaData databaseMetaData = dataSource.getConnection().getMetaData();
        ResultSet tableRet = databaseMetaData.getTables(null, "%",originalTableName,new String[]{"TABLE"});

        String tableName = "";
        String remarks = "";
        List<TableColumnMetaData> columnMetaList = new ArrayList<>();
        while(tableRet.next()) {
            tableName = tableRet.getString("TABLE_NAME");
            remarks = tableRet.getString("REMARKS");
        }
        ResultSet rs = databaseMetaData.getColumns(null,"%", originalTableName,"%");
        while (rs.next()) {
            Map<String,Object> columnMetaData = new HashMap<>(16);
            for(int i = 1;i<=rs.getMetaData().getColumnCount();i++){
                String columnName = rs.getMetaData().getColumnName(i);
                Object value = rs.getObject(columnName);
                columnMetaData.put(columnName,value);
            }
            TableColumnMetaData tableColumnMetaData = JSON.toJavaObject(new JSONObject(columnMetaData), TableColumnMetaData.class);
            columnMetaList.add(tableColumnMetaData);
        }

        ResultSet indexInfo = databaseMetaData.getIndexInfo(null, "%", originalTableName, false, true);

        List<IndexMetaData> metaDataList = new ArrayList<>();

        while (indexInfo.next()) {
            Map<String, Object> columnMetaData = new HashMap<>(16);
            for (int i = 1; i <= indexInfo.getMetaData().getColumnCount(); i++) {
                String columnName = indexInfo.getMetaData().getColumnName(i);
                Object value = indexInfo.getObject(columnName);
                columnMetaData.put(columnName, value);
            }
            IndexMetaData indexMetaData = JSON.toJavaObject(new JSONObject(columnMetaData), IndexMetaData.class);
            metaDataList.add(indexMetaData);
        }
        TableMetaData tableMetaData = new TableMetaData(tableName,remarks,columnMetaList,metaDataList);
        return tableMetaData;
    }

}
