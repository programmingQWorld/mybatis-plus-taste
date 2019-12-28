package im.lincq.mybatisplus.taste.test.mysql;

import im.lincq.mybatisplus.taste.mapper.AutoSqlInjector;
import im.lincq.mybatisplus.taste.toolkit.TableInfo;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

public class MySqlInjector  extends AutoSqlInjector {

    @Override
    public void inject(Configuration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass,
                       Class<?> modelClass, TableInfo table) {
        /* 添加一个自定义方法 */
        deleteAllUser(mapperClass, modelClass, table);
    }

    public void deleteAllUser (Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        /* 执行 SQL， 动态 SQL （包含一些）   参考  类 Sql */
        String sql = "delete from " + table.getTableName();

        /* mapper 接口 方法名一直 */
        String method = "deleteAll";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addDeleteMappedStatement(mapperClass, method, sqlSource);
    }
}
