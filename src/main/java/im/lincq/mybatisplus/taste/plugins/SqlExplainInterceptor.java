package im.lincq.mybatisplus.taste.plugins;


import im.lincq.mybatisplus.taste.exceptions.MybatisPlusException;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Logger;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class })
})
public class SqlExplainInterceptor implements Interceptor {

    protected final Logger logger = Logger.getLogger("SqlExplainInterceptor");

    /**
     * 发现执行全表 delete update 语句是否停止执行
     */
    private boolean stopProceed = false;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        /**
         * 处理DELETE UPDATE语句
         */

        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        if (ms.getSqlCommandType() == SqlCommandType.DELETE || ms.getSqlCommandType() == SqlCommandType.UPDATE) {
            Configuration configuration = ms.getConfiguration();
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = ms.getBoundSql(parameter);
            Executor exe = (Executor)invocation.getTarget();
            Connection connection = exe.getTransaction().getConnection();

            /**
             * 执行SQL分析
             */
            sqlExplain(configuration, ms, boundSql, connection, parameter);
        }

        return null;
    }

    /**
     * 判断是否执行SQL
     * */
    protected void sqlExplain( Configuration configuration, MappedStatement mappedStatement, BoundSql boundSql,
                               Connection connection, Object parameter) throws Exception {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            StringBuilder explain = new StringBuilder("EXPLAIN ");
            explain.append(boundSql.getSql());
            String sqlExplain = explain.toString();
            StaticSqlSource sqlSource = new StaticSqlSource(configuration, sqlExplain, boundSql.getParameterMappings());
            MappedStatement.Builder builder = new MappedStatement.Builder(configuration, "explain_sql", sqlSource, SqlCommandType.SELECT);
            builder.resultMaps(mappedStatement.getResultMaps()).resultSetType(mappedStatement.getResultSetType()).statementType(mappedStatement.getStatementType());
            MappedStatement queryStatement = builder.build();
            DefaultParameterHandler handler = new DefaultParameterHandler(queryStatement, parameter, boundSql);
            stmt = connection.prepareStatement(sqlExplain);
            handler.setParameters(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                if (!"Using where".equals(rs.getString("Extra"))) {
                    String tip = "Full table operation is prohibited. SQL:" + boundSql.getSql();
                    if (this.isStopProceed()) {
                        throw new MybatisPlusException(tip);
                    }
                    logger.severe(tip);
                    break;
                }
            }

        } catch ( Exception e ) {
            throw new MybatisPlusException(e);
        } finally {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        }

    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof  Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public boolean isStopProceed () {
        return stopProceed;
    }

    public void setStopProceed(boolean stopProceed) {
        this.stopProceed = stopProceed;
    }
}
