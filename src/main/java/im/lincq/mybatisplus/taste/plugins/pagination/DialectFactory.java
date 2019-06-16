package im.lincq.mybatisplus.taste.plugins.pagination;

import im.lincq.mybatisplus.taste.plugins.pagination.dialects.*;

/**
 * 数据库分页方言工厂类
 */
public class DialectFactory {
    public static IDialect getDialectByDbtype (String dbtype) throws Exception {
        if ("mysql".equalsIgnoreCase(dbtype)) {
            return new MySqlDialect();
        } else if ("oracle".equalsIgnoreCase(dbtype)) {
            return new OracleDialect();
        } else if ("hsql".equalsIgnoreCase(dbtype)) {
            return new HSQLDialect();
        } else if ("sqlite".equalsIgnoreCase(dbtype)) {
            return new SQLiteDialect();
        } else if ("postgre".equalsIgnoreCase(dbtype)) {
            return new PostgreDialect();
        } else if ("sqlserver".equalsIgnoreCase(dbtype)) {
            return new SQLServerDialect();
        } else {
            return null;
        }
    }
}
