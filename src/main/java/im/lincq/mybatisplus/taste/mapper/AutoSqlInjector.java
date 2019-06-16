package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.annotations.IdType;
import im.lincq.mybatisplus.taste.toolkit.TableFieldInfo;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * <p>
 *SQL自动注入器
 * </p>
 */
public class AutoSqlInjector {
    private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();

    /** mybatis配置类对象， 还有一个小助理*/
    private Configuration configuration;
    private MapperBuilderAssistant assistant;

    /**
     * 注入单点SQL
     * @param mapperClass
     */
    public void inject(Class<?> mapperClass) {
        System.out.println("执行sql语句注入方法");
        assistant = new MapperBuilderAssistant(configuration, mapperClass.getName().replaceAll("\\.", "/"));
        assistant.setCurrentNamespace(mapperClass.getName());

        Class<?> modelClass  = extractModelClass(mapperClass);
        TableInfo table = TableInfoHelper.getTableInfo(modelClass);

        /* 没有指定主键，默认方法不能使用(lincq: 没有主键，不给这个mapperClass增强) */
        if (table.getKeyProperty() != null) {

            /* 插入 */
            this.injectInsertSql(false, mapperClass, modelClass, table);
            this.injectInsertSql(true, mapperClass, modelClass, table);

            /* 删除 */
            this.injectDeleteSelectiveSql(mapperClass, modelClass, table);
            this.injectDeleteSql(false, mapperClass, modelClass, table);
            this.injectDeleteSql(true, mapperClass, modelClass, table);
            System.out.println("The modelClass is  (User ???)" + modelClass);

            /* 修改 */
            this.injectUpdateSql(mapperClass, modelClass, table);

            /* 查询 */
            this.injectSelectSql(false, mapperClass, modelClass, table);
            this.injectSelectSql(true, mapperClass, modelClass, table);
            this.injectSelectOneSql(mapperClass, modelClass, table);
            this.injectSelectListSql(mapperClass, modelClass, table);

        } else {
            /*
             *  提示:主键属性未知
             *  */
            System.err.println(String.format("%s, The unknown primary key, cannot use the generic method",
                    mapperClass.toString()));
        }

    }

    /**
     * 注入查询SQL语句
     * @param batch  是否批量查询
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    private void injectSelectSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_BY_ID;
        SqlSource sqlSource = null;
        String sql = null;
        if (batch) {
            sqlMethod = SqlMethod.SELECT_BATCH;
            StringBuilder ids = new StringBuilder();
            ids.append("\n<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">\n#{item}</foreach>");
            sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table), table.getTableName(), table.getKeyColumn(), ids.toString());
            sqlSource = languageDriver.createSqlSource(configuration, sql, mapperClass);
        } else {
            sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table), table.getTableName(), table.getKeyColumn(), table.getKeyProperty());
            sqlSource = new RawSqlSource(configuration, sql, mapperClass);
        }
        System.out.println("inject select(batch) sql: " + sql);
        this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.SELECT, modelClass);
    }

    /**
     * 注入实体查询 SQL 语句
     */
    private void injectSelectOneSql (Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_ONE;
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table), table.getTableName(), sqlWhere(table));
        System.out.println("inject select (one) sql("+ sqlMethod.getMethod() +")：" + sql);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addMappedStatement(mapperClass, sqlMethod, sqlSource,SqlCommandType.SELECT, modelClass);
    }

    /**
     * <p>注入实体查询记录列表SQL语句</p>
     *
     * @param mapperClass       接口类型
     * @param modelClass          实体类型
     * @param table                     表
     */
    private void injectSelectListSql (Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_LIST;
        StringBuilder where = new StringBuilder("\n<if test = \"ew != null\">");
        where .append("<if test = \"ew.table != null\"");

        where.append("\n<where>\n");
        // # 主键         条件拼接
        where .append("<if test = \"ew.entity.").append(table.getKeyProperty()).append("!= null\">");
        where.append(table.getKeyColumn()).append("#{ew.entity.").append(table.getKeyProperty()).append("}");
        where .append("\n</if>\n");

        // # 对象属性  条件拼接
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            where .append("<if test = \"ew.entity.").append(fieldInfo.getProperty()).append("!= null\">");
            where.append(fieldInfo.getColumn()).append("=#{ew.entity.").append(fieldInfo.getProperty()).append("}");
            where .append("</if>\n");
        }

        where.append("</where>\n");

        // # order by  拼接
        where.append("<if test = \"ew.orderByField != null\"> ORDER BY #{ew.orderByField}</if>\n");
        where.append("\n</if>");
        where .append("\n</if>");
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table), table.getTableName(), where.toString());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        System.out.println("inject select (list by ew) : " + sql);
        this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.SELECT, modelClass);

    }

    private void addMappedStatement (Class<?> mapperClass, String id, SqlSource sqlSource, SqlCommandType sqlCommandType, Class<?> resultType) {
        this.addMappedStatement(mapperClass, id, sqlSource,
                sqlCommandType, null, resultType,
                new NoKeyGenerator(), null, null);
    }

    /**
     * <p>
     *     注入 insert sql语句
     * </p>
     *
     * @param batch                是否为批量插入
     * @param mapperClass  Mapper Class对象
     * @param modelClass    MapperClass对应的实体类Class对象
     * @param table                表名
     */
    private void injectInsertSql (boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {

        /*
		 * INSERT INTO table
		 * <trim prefix="(" suffix=")" suffixOverrides=",">
		 * 		<if test="xx != null">xx,</if>
		 * </trim>
		 * VALUES
		 * <trim prefix="values (" suffix=")" suffixOverrides=",">
		 * 		<if test="xx != null">#{xx},</if>
		 * </trim>
		 */

        KeyGenerator keyGenerator = new NoKeyGenerator();
        StringBuilder fieldBuilder = new StringBuilder();
        StringBuilder placeholderBuilder = new StringBuilder();
        SqlMethod sqlMethod = SqlMethod.INSERT_ONE;
        if (batch) {
            sqlMethod = SqlMethod.INSERT_BATCH;
        }

        fieldBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        placeholderBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");

        // keyProperty 主键 属性名
        // keyColumn  主键 字段名
        String keyProperty = table.getKeyProperty();
        String keyColumn = table.getKeyColumn();

        if (table.getIdType() == IdType.AUTO) {
            /* 自增主键 */
            keyGenerator = new Jdbc3KeyGenerator();
            keyColumn = table.getKeyColumn();
            keyProperty = table.getKeyProperty();
        } else {
            /* 用户输入ID*/
            fieldBuilder.append(table.getKeyColumn()).append(",");
            placeholderBuilder.append("#{").append(batch ? "item." : "");
            placeholderBuilder.append(table.getKeyProperty()).append("},");
        }

        List<TableFieldInfo> fieldLists = table.getFieldList();

        int size = fieldLists.size();
        // 拼接出字符串
        for (TableFieldInfo fieldInfo : fieldLists) {
            // xxx if batch : sql => #{item.xx}
            // xxx or not   : sql => #{xx}
            // 这里还有一个逻辑，我在前阵子写过自己的代码生成，看到这里的变化，有一些明白
            // if batch 所有的字段都会使用，不进行实体属性空判断。（因为我们没办法保证每个实体中，成员变量都是一致空或非空）
            // or not    进行属性空判断。

            if (!batch) {
                // # 字段
                fieldBuilder
                        .append("\n<if test=\"").append(fieldInfo.getProperty()).append(" != null\">");
                // # 占位符
                placeholderBuilder
                        .append("\n<if test=\"").append(fieldInfo.getProperty()).append(" != null\">");
            }

            fieldBuilder.append(fieldInfo.getColumn()).append(",");
            placeholderBuilder.append("#{").append(batch ? "item." : "");
            placeholderBuilder.append(fieldInfo.getProperty()).append("},");

            if (!batch) {
                fieldBuilder
                        .append("</if>");
                placeholderBuilder
                        .append("</if>\n");
            }

        }

        fieldBuilder.append("\n</trim>\n");
        placeholderBuilder.append("\n</trim>\n");

        String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(), placeholderBuilder.toString());
        System.out.println("inject insert(batch) sql: " + sql);
        SqlSource sqlSource  = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator, keyProperty, keyColumn);
    }

    public AutoSqlInjector (Configuration configuration) {
        // object父类的构造方法，在这里应该没有什么用处.
        super();
        this.configuration = configuration;
    }

    /**
     * <p>注入更新SQL语句</p>
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    private void injectUpdateSql (Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.UPDATE_BY_ID;
        StringBuilder set = new StringBuilder();
        List<TableFieldInfo> fieldList = table.getFieldList();
        int size = fieldList.size();

        /*
		 * UPDATE table
		 * <trim prefix="SET" suffixOverrides="," suffix="WHERE id=#{id}" >...</trim>
		 */
        set.append("<trim prefix=\"SET\" suffixOverrides=\",\" suffix=\"WHERE ");
        set.append(table.getKeyColumn()).append("=#{").append(table.getKeyProperty()).append("}\">");

        for (TableFieldInfo fieldInfo : fieldList) {
            set.append("<if test=\"#{").append(fieldInfo.getProperty()).append(" != null }\">\n");
            set.append(fieldInfo.getColumn()).append("=#{").append(fieldInfo.getProperty()).append("}").append(",");
            set.append("\n</if>");
        }
        set.append("\n</trim>");

        String sql = String.format(sqlMethod.getSql(), table.getTableName(), set.toString());
        System.out.println("inject update sql: " + sql);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, mapperClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    private void injectDeleteSelectiveSql (Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.DELETE_SELECTIVE;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhere(table));
        System.out.println("inject delete selective sql : " + sql);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.DELETE, null);
    }

    /**
     * 注入删除 SQL 语句
     * @param batch  是否批量
     */
    private void injectDeleteSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.DELETE_BY_ID;
        SqlSource sqlSource = null;
        if (batch) {
            sqlMethod = SqlMethod.DELETE_BATCH;
            StringBuilder ids = new StringBuilder();
            ids.append("\n<foreach item=\"item\" index=\"index\" collection=\"list\" separator=\",\">");
            ids.append("#{item}");
            ids.append("\n</foreach>");
            String sql = String.format(sqlMethod.getSql(), table.getTableName(), table.getKeyColumn(), ids.toString());
            sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        } else {
            String sql = String.format(sqlMethod.getSql(), table.getTableName(), table.getKeyColumn(), table.getKeyProperty());
            sqlSource = new RawSqlSource(configuration, sql, Object.class);
        }
        //System.out.println("inject delete(batch) sql: " + sqlSource.getBoundSql(null).getSql());
        this.addMappedStatement(mapperClass, sqlMethod, sqlSource, SqlCommandType.DELETE, null);
    }

    /**
     *
     * @param mapperClass
     * @param sm
     * @param sqlSource
     * @param sqlCommandType  类似于 xml 配置标签中的 标签名称
     * @param resultType                类似于 xml 配置标签中的 返回类型
     * @return
     */
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
            System.err.println(statementName + ", Has been loaded by XML or SqlProvider, ignoring the injection of the SQL.");
            return null;
        }
        return assistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
                parameterClass, null, resultType, null, false, true, false, keyGenerator, keyProperty, keyColumn,
                configuration.getDatabaseId(), new XMLLanguageDriver(), null);
    }

    private void addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource source, KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        this.addInsertMappedStatement(mapperClass, id, source, SqlCommandType.INSERT, modelClass, null, keyGenerator, keyProperty, keyColumn);
    }

    /**
     *
     * @param mapperClass
     * @param id
     * @param source
     * @param sqlCommandType
     * @param parameterClass
     * @param resultType
     * @param keyGenerator
     * @param keyProperty   主键属性名称
     * @param keyColumn    主键字段名称
     */
    private void addInsertMappedStatement (Class<?> mapperClass, String id, SqlSource source, SqlCommandType sqlCommandType, Class<?> parameterClass,  Class<?> resultType, KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        String statementName = mapperClass.getName() + "." + id;
        if (configuration.hasStatement(statementName)) {
            System.err.println(statementName + "已通过Xml或SqlProvider加载了,忽略该SQL的注入");
            return;
        }

        assistant.addMappedStatement(
                id, source, StatementType.PREPARED, sqlCommandType,
                null, null, null, parameterClass, null,
                resultType, null, false, true, false,
                keyGenerator, keyProperty, keyColumn, configuration.getDatabaseId(), new XMLLanguageDriver(), null);
    }

    /**
     * SQL 查询所有表字段
     */
    private String sqlSelectColumns (TableInfo table) {

        // 获取主键,属性
        StringBuilder columns = new StringBuilder();
        if (table.isKeyRelated()) {
            columns.append(table.getKeyColumn()).append(" AS ").append(table.getKeyProperty());
        } else {
            columns.append(table.getKeyProperty());
        }
        List<TableFieldInfo> fieldList = table.getFieldList();

        for (TableFieldInfo fieldInfo : fieldList) {
            columns.append(",").append(fieldInfo.getColumn());
            if (fieldInfo.isRelated()) {
                columns.append(" AS ").append(fieldInfo.getProperty());
            }
        }
        return columns.toString();
    }

    /**
     * SQL 查询条件
     */
    private String sqlWhere (TableInfo table) {
        StringBuilder where = new StringBuilder();
        where.append("<where>");
        // # 遍历成员属性
        // 主键成员
        where.append("\n<if test=\"").append(table.getKeyProperty()).append(" != null\">");
        where.append("\n").append(table.getKeyColumn()).append(" = #{").append(table.getKeyProperty()).append("}");
        where.append("\n</if>");
        // 其它成员
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            where.append("\n<if test=\"").append(fieldInfo.getProperty()).append(" != null\">");
            where.append("\nAND  ").append(fieldInfo.getColumn()).append(" = #{").append(fieldInfo.getProperty()).append("}");
            where.append("\n</if>");
        }
        where.append("</where>");
        return where.toString();
    }

    private Class<?> extractModelClass (Class<?> mapperClass) {
        // 返回这个类/接口中被直接实现的接口的类型（Types）。
        Type[] types = mapperClass.getGenericInterfaces();
        ParameterizedType target = null;
        for (Type type : types) {
            // 1. 有泛型参数 && 类型为AutoMapper.class
            if (type instanceof ParameterizedType && ((ParameterizedType)type).getRawType().equals(AutoMapper.class) ) {
                target = (ParameterizedType)type;
                break;
            }
        }
        /*
        getActualTypeArguments方法：
        返回Type对象的数组，表示此对象的实际类型参数(type args，
        指的应该就是泛型参数的实际类型). 注意在某些情况下，返回的数组为空，比如类型无泛型约束
        */
        Type[] parameters = target.getActualTypeArguments();
        return (Class<?>)parameters[0];
    }
}