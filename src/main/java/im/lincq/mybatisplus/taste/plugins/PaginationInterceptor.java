package im.lincq.mybatisplus.taste.plugins;

import im.lincq.mybatisplus.taste.exceptions.MybatisPlusException;
import im.lincq.mybatisplus.taste.plugins.pagination.DialectFactory;
import im.lincq.mybatisplus.taste.plugins.pagination.IDialect;
import im.lincq.mybatisplus.taste.plugins.pagination.Pagination;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 分页拦截器
 */
@Intercepts({@Signature(type = StatementHandler.class, method="prepare", args = {Connection.class, Integer.class })})
public class PaginationInterceptor implements Interceptor {

    private static final long serialVersionUID = 1L;

    /** 方言类型 */
    private String dialectType;
    /** 方言实现类 */
    private String dialectClazz;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if (target instanceof StatementHandler) {
            StatementHandler statementHandler = (StatementHandler)target;
            // SystemMetaObject.应该是一个反射帮助类，获取类对象信息更加方便
            MetaObject metaStatementHandler =  SystemMetaObject.forObject(statementHandler);
            // 这是什么意思呢? | --20190317 理解RowBounds对象作用之后，再结合分页拦截器逻辑及其测试方式就明白了.
            RowBounds rowBounds = (RowBounds)metaStatementHandler.getValue("delegate.rowBounds");

            /* 不需要分页的场合，是否需要分页在这里判断. */
            if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
                return invocation.proceed();
            }

            /* 定义数据库方言 */
            IDialect dialect = null;
            if (dialectType != null && !"".equals(dialectType)) {
                dialect = DialectFactory.getDialectByDbtype(dialectType);
            } else {
                /*
                 * 从这里可以看出，字符串类型dialectClazz配置了分页方言类，
                 * 配置方式可以是配置文件中指定
                 * dialectClazz: "im.lincq.mybatisplus.taste.plugins.pagination.dialects.MySqlDialect"*/
                if (dialectClazz != null && !"".equals(dialectClazz)) {
                    try {
                        Class<?> clazz = Class.forName(dialectClazz);
                        if (IDialect.class.isAssignableFrom(clazz)) {
                            dialect = (IDialect) clazz.newInstance();
                        }
                    } catch (ClassNotFoundException e) {
                        throw new MybatisPlusException("Class: "+ dialectClazz + "is not found");
                    }
                }
            }

            /* 未配置方言则抛出异常 */
            if (dialect == null) {
                throw new MybatisPlusException("The value of the dialect property in mybatis configuration.xml is not defined.");
            }
            /* 获取待分页sql语句进行分页组装 */
            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            String originalSql = boundSql.getSql();

            /* 禁用内存分页  （内存分页会查询所有结果出来，这个是很吓人的，如果结果变化频繁，这个数据还会不准）*/
            metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
            metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);

            /* <p>分页逻辑</p> 判断是否需要查询总记录条数  count */
            if (rowBounds instanceof Pagination) {
                // invocation.getTarget();    // 获取被拦截的类当前实例
                // invocation.getMethod(); // 获取被拦截的方法的方法对象
                // invocation.getArgs();       // 获取被拦截方法的方法参数
                // # 定义分页查询（Pagination→count()）
                MappedStatement mappedStatement = (MappedStatement)metaStatementHandler.getValue("delegate.mappedStatement");
                Connection connection = (Connection)invocation.getArgs()[0];
                Pagination page = (Pagination)rowBounds;
                if (page.isSearchCount()) {
                    page = this.count(originalSql, connection, mappedStatement, boundSql, page);
                    /** 总数 0 跳出执行 */
                    if (page.getTotal() <= 0) {
                        return invocation.proceed();
                    }
                }
                originalSql = dialect.buildPaginationSql(originalSql, page.getOffsetCurrent(), page.getSize());
            }
            /* 将组装分页后的sql写回对应的statementhandler */
            metaStatementHandler.setValue("delegate.boundSql.sql", originalSql);
        }

        // proceed 继续进行.
        return invocation.proceed();
    }

    /**
     * 查询总记录条数
     * @param sql
     * @param connection
     * @param mappedStatement
     * @param boundSql
     */
    public Pagination count (String sql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql, Pagination page) {

        String sqlUse = sql;

        /* 这部分优化影响了count函数的正常执行 */
        /*int order_by = sql.toUpperCase().indexOf("ORDER BY");
        if (order_by > -1) {
            sqlUse = sql.substring(0, order_by);
        }*/
        // sql: 获取总记录数量
        StringBuffer countSql = new StringBuffer("SELECT COUNT(1) AS TOTAL FROM (");
        countSql.append(sqlUse).append(") A");

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try  {
            pstmt = connection.prepareStatement(countSql.toString());
            // 下面的这2句不太懂意思.或许是跟sql语句参数设置相关.
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql.toString(),
                    boundSql.getParameterMappings(), boundSql.getParameterObject());
            ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), countBS);
            parameterHandler.setParameters(pstmt);
            rs = pstmt.executeQuery();
            int total = 0;
            if (rs.next()) {
                total = rs.getInt(1);
            }
            page.setTotal(total);

            /**
             * 当前页大于总页数，当前页设置为第一页
             */
            if (page.getCurrent() > page.getTotal()) {
                page = new Page(1, page.getSize());
                page.setTotal(total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
             try {
                 rs.close();
                 pstmt.close();
             } catch (SQLException e) {
                 e.printStackTrace();
             }
        }
        return page;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties prop) {
        String dialectType = prop.getProperty("dialectType");
        String dialectClazz = prop.getProperty("dialectClazz");

        if (dialectType != null && !"".equals(dialectType)) {
            this.dialectType = dialectType;
        }
        if (dialectClazz != null && !"".equals(dialectClazz)) {
            this.dialectClazz = dialectClazz;
        }
    }
}
                                                  