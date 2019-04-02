package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.toolkit.TableInfo;
import im.lincq.mybatisplus.taste.toolkit.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;


public class AutoSqlInjector {
    private transient Logger logger = LoggerFactory.getLogger(getClass());
    private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();

    /** mybatis配置类对象， 还有一个小助理*/
    private Configuration configuration;
    private MapperBuilderAssistant assistant;

    public AutoSqlInjector (Configuration configuration) {
        // object父类的构造方法，在这里应该没有什么用处.
        super();
        this.configuration = configuration;
    }

    public void inject(Class<?> mapperClass) {
        System.out.println("执行sql语句注入方法");
        assistant = new MapperBuilderAssistant(configuration, mapperClass.getName().replaceAll("\\.", "/"));
        assistant.setCurrentNamespace(mapperClass.getName());

        Class<?> modelClass  = extractModelClass(mapperClass);
        TableInfo table = TableInfoHelper.getTableInfo(modelClass);


        /* 新增 */
        this.injectInsertSql(false, mapperClass, modelClass, table);
        this.injectInsertSql(true, mapperClass, modelClass, table);


        /* 没有指定主键，默认忽略主键修改，删除，查询方法 */
        if (table.getTableId() != null) {
            /* 根据主键修改，主键名默认为id */
            this.injectUpdateSql(mapperClass, modelClass, table);

            /* 根据主键删除，主键名默认为id */
            SqlSource sqlSource = new RawSqlSource(configuration,
                    String.format(SqlMethod.DELETE_ONE.getSql(), table.getTableName(), table.getTableId()),
                    Object.class);
            this.addMappedStatement(mapperClass,
                    SqlMethod.DELETE_ONE.getMethod(), sqlSource,
                    SqlCommandType.DELETE, null);

            /*  根据主键查找，主键名默认为id*/
            sqlSource = new RawSqlSource(configuration,
                    String.format(SqlMethod.SELECT_ONE.getSql(), table.getTableName(), table.getTableId()),
                    Object.class);
            this.addMappedStatement(mapperClass, SqlMethod.SELECT_ONE.getMethod(),
                    sqlSource, SqlCommandType.SELECT, modelClass);
        }
        /* 查询全部 */
        SqlSource sqlSource = new RawSqlSource(configuration,
                String.format(SqlMethod.SELECT_ALL.getSql(), table.getTableName()),
                null);
        System.out.println("inject select-all sql: "
                + String.format(SqlMethod.SELECT_ALL.getSql(), table.getTableName()));

        this.addMappedStatement(mapperClass,
                SqlMethod.SELECT_ALL.getMethod(), sqlSource,
                SqlCommandType.SELECT, modelClass);
    }

    public void addMappedStatement (Class<?> mapperClass, String id, SqlSource sqlSource, SqlCommandType sqlCommandType, Class<?> resultType) {
        this.addMappedStatement(mapperClass, id, sqlSource,
                sqlCommandType, null, resultType,
                new NoKeyGenerator(), null, null);
    }

    /**
     * 注入 insert sql语句
     * @param batch              是否批量插入
     * @param mapperClass  Mapper Class对象
     * @param modelClass    MapperClass对应的实体类Class对象
     * @param table                表名
     */
    private void injectInsertSql (boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {

        KeyGenerator keyGenerator = new NoKeyGenerator();
        StringBuilder fieldBuilder = new StringBuilder();
        StringBuilder placeholderBuilder = new StringBuilder();
        SqlMethod sqlMethod = SqlMethod.INSERT_ONE;
        if ( batch ) {
            sqlMethod = SqlMethod.INSERT_BATCH;
            // 批量增加，使用的是mybatis - mapper文件中的 foreach,
            // 如果有相关括号的疑问，请不要怀疑，它在下面的builder中将会补充
            placeholderBuilder.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">(");
        }

        String keyParam = null;
        if (table.getTableId() != null) {
            /* 自增主键 */
            if (table.isAutoIncrement()) {
                keyGenerator = new Jdbc3KeyGenerator();
                keyParam =table.getTableId();
            } else {
                /* 非自增，用户生成 */
                fieldBuilder.append(table.getTableId()).append(",");
                // 批量情况下，sql语句会有所不同
                if (batch) {
                    placeholderBuilder.append("#{item.");
                } else {
                    placeholderBuilder.append("#{");
                }
                placeholderBuilder.append(table.getTableId()).append("},");
            }

        }


        List<String> fieldLists = table.getFieldList();

        int size = fieldLists.size();
        // 拼接出字符串
        for (int i=0; i<size; i++) {
            String fieldName = fieldLists.get(i);
            fieldBuilder.append(fieldName);

            // if batch : sql => #{item.xx}
            // or not   : sql => #{xx}
            placeholderBuilder.append("#{");
            if ( batch ) {
                placeholderBuilder.append("item.");
            }
            placeholderBuilder.append(fieldName).append("}");

            if (i < size - 1) {
                fieldBuilder.append(",");
                placeholderBuilder.append(",");
            }
        }

        if ( batch ) {
            placeholderBuilder.append(")\n</foreach>");
        }

        String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(), placeholderBuilder.toString());
        System.out.println("inject insert(batch) sql: " + sql);
        SqlSource sqlSource  = null;
        if ( batch ) {
            sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        } else {
            sqlSource = new RawSqlSource(configuration, sql, modelClass);
        }
        this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator, keyParam, keyParam);
    }

    /**
     * <p>注入更新SQL语句</p>
     */
    private void injectUpdateSql (Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ").append(table.getTableName()).append(" SET ");
        List<String> fieldList = table.getFieldList();
        int size = fieldList.size();
        for (int i = 0; i < size; i++) {
            String fieldName = fieldList.get(i);
            sqlBuilder.append(fieldName).append("=#{").append(fieldName).append("}");
            if (i < size - 1) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(" WHERE ").append(table.getTableId()).append("=#{").append(table.getTableId()).append("}");
        System.out.println("inject update sql: " + sqlBuilder.toString());
        SqlSource sqlSource = new RawSqlSource(configuration, sqlBuilder.toString(), mapperClass);
        this.addUpdateMappedStatement(mapperClass, modelClass,
                SqlMethod.UPDATE_ONE.getMethod(), sqlSource);
    }

    private MappedStatement addMappedStatement(Class<?> mapperClass, SqlMethod sm, SqlSource sqlSource,
        SqlCommandType sqlCommandType, Class<?> resultType) {
        return this.addMappedStatement(mapperClass, sm.getMethod(), sqlSource, sqlCommandType, null, resultType,
                new NoKeyGenerator(), null, null);
    }



    private void addUpdateMappedStatement (Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource) {
        this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null,
                new NoKeyGenerator(), null, null);
    }

    private MappedStatement addMappedStatement (Class<?> mapperClass, String id, SqlSource sqlSource, SqlCommandType sqlCommandType,
                                     Class<?> parameterClass, Class<?> resultType, KeyGenerator keyGenerator, String keyProperty, String keyColumn ) {
        String statementName = mapperClass.getName() + "." + id;
        if (configuration.hasStatement(statementName)) {
            logger.warn("{}, Has been loaded by XML or SqlProvider, ignoring the injection of the SQL.", statementName);
            return null;
        }
        return assistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
                parameterClass, null, resultType, null, false, true, false, keyGenerator, keyProperty, keyColumn,
                configuration.getDatabaseId(), new XMLLanguageDriver(), null);
    }

    private void addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource source, KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        this.addInsertMappedStatement(mapperClass, id, source, SqlCommandType.INSERT, modelClass, null, keyGenerator, keyProperty, keyColumn);
    }

    private void addInsertMappedStatement (Class<?> mapperClass, String id, SqlSource source, SqlCommandType sqlCommandType, Class<?> parameterClass,  Class<?> resultType, KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        String statementName = mapperClass.getName() + "." + id;
        if (configuration.hasStatement(statementName)) {
            logger.warn("{}, 已通过Xml或SqlProvider加载了,忽略该SQL的注入", statementName);
            return;
        }

        assistant.addMappedStatement(
                id, source, StatementType.PREPARED, sqlCommandType,
                null, null, null, parameterClass, null,
                resultType, null, false, true, false,
                keyGenerator, keyProperty, keyColumn, configuration.getDatabaseId(), new XMLLanguageDriver(), null);
    }

    private Class<?> extractModelClass (Class<?> mapperClass) {
        // 返回这个类/接口中被直接实现的接口的类型（Types）。
        Type[] types = mapperClass.getGenericInterfaces();
        ParameterizedType target = null;
        for (Type type : types) {
            // 1. 有泛型参数 && 类型为AutoMapper.class
            if (type instanceof ParameterizedType
                    && ((ParameterizedType)type).getRawType().equals(AutoMapper.class) ) {
                target = (ParameterizedType)type;
                break;
            }
        }
        /*
        getActualTypeArguments方法：
        返回Type对象的数组，表示此对象的实际类型参数(type args，指的应该就是泛型参数的实际类型). 注意在某些情况下，返回的数组为空，比如类型无泛型约束
        */
        Type[] parameters = target.getActualTypeArguments();
        return (Class<?>)parameters[0];
    }
}
