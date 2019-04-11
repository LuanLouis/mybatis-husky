package com.luanlouis;

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
import com.luanlouis.jdbc.instropect.IntrospectUtils;
import com.luanlouis.jdbc.instropect.TableMetaData;
import com.luanlouis.mybatis.husky.TestBootApplication;
import com.luanlouis.mybatis.husky.mapper.SaleOrderMapper;
import com.luanlouis.mybatis.sharding.strategy.DataSourceBasedShardingTableStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author terrylouis1991@gmail.com
 * @date 2019/03/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestBootApplication.class)
@MapperScan(basePackages = "com.luanlouis.mybatis.husky.mapper")
public class ShardingTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    @Test
    public void test() throws SQLException {

        DataSourceBasedShardingTableStrategy dataSourceBasedShardingTableStrategy = new DataSourceBasedShardingTableStrategy(this.dataSource);
        dataSourceBasedShardingTableStrategy.route("T_TMP_USER_LIST","000000000012");
        TableMetaData tableMetaData = IntrospectUtils.introspect("SALE_ORDER",this.dataSource);
        System.out.println(tableMetaData.getCreateTableSql());
        System.out.println(tableMetaData.getIndexSql());
    }



    @Test(expected = Exception.class)
    public void test3(){
        saleOrderMapper.selectAll("0000000001");
    }

//    @Test
    public void test2(){

        String sql = "  SELECT A.ATTR_VALUE\n" +
                "            FROM (select * from t_attr_value where TENANT_ID = #{tenantId}) as A\n" +
                "                   LEFT JOIN t_attr_def as B ON A.ATTR_KEY = B.ATTR_ID\n" +
                "            WHERE A.TENANT_ID = #{tenantId}\n" +
                "              and A.BELONG_ID =  #{belongId}\n" +
                "              AND B.ATTR_KEY = #{attrKey}\n" +
                "            ORDER BY B.TENANT_ID DESC\n" +
                "            LIMIT 0,1";

        String dbType = JdbcConstants.MYSQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SQLStatement stmt = stmtList.get(0);
        StringBuilder stringBuilder = new StringBuilder();
        SQLASTOutputVisitor visitor = new TableRoutingMySqlAdaptor(stringBuilder, "zxcv");
//        SQLASTOutputVisitor visitor = new MySqlOutputVisitor(stringBuilder, true);
        stmt.accept(visitor);

        System.out.println(stringBuilder.toString());
    }
    class TableRoutingMySqlAdaptor extends MySqlOutputVisitor {

        private String tenantId;

        TableRoutingMySqlAdaptor(Appendable appender, String tenantId) {
            super(appender,true);
            this.tenantId = tenantId;
        }

        @Override
        protected void printAlias(String alias) {
            super.printAlias(alias);
        }

        @Override
        protected void printTableSource(SQLTableSource x) {
            Class<?> clazz = x.getClass();
            if (clazz == SQLJoinTableSource.class) {
                super.visit((SQLJoinTableSource) x);
            } else if (clazz == SQLExprTableSource.class) {
                printSQLExprTableSource(x);
            } else if (clazz == SQLSubqueryTableSource.class) {
                super.visit((SQLSubqueryTableSource) x);
            } else {
                x.accept(this);
            }
        }

        void printSQLExprTableSource(SQLTableSource tableSource) {
            if (tableSource.getClass() == SQLExprTableSource.class) {
                SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) tableSource;
                String oldTableName = sqlExprTableSource.getName().getSimpleName();
                String newTableName = oldTableName;
                if(StringUtils.isEmpty(tableSource.getAlias())){
                    print0(newTableName);
                }else{
                    print0(newTableName +" as " + tableSource.getAlias());
                }
            }
        }
    }
}
