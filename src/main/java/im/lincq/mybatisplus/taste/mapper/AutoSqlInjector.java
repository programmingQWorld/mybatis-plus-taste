package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.MybatisConfiguration;
import im.lincq.mybatisplus.taste.annotations.FieldStrategy;
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
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.session.Configuration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>
 *SQL自动注入器
 * </p>
 * @author lin-cq
 */
public class AutoSqlInjector  implements  ISqlInjector {
    private final Logger logger = Logger.getLogger("AutoSqlInjector");

    /** mybatis配置类对象， 还有一个小助理*/
    protected Configuration configuration;
    protected MapperBuilderAssistant builderAssistant;
    protected LanguageDriver languageDriver;
    protected DBType dbType = DBType.MYSQL;

    public AutoSqlInjector () {}

    public AutoSqlInjector (Configuration configuration, DBType dbType) {
        this.configuration = configuration;
        this.dbType = dbType;
    }

    /**
     * CRUD诸如后给予标识，注入过后不再注入
     * @param configuration
     * @param mapperBuilderAssistant
     * @param mapperClass
     */
    public void inspectInject(Configuration configuration, MapperBuilderAssistant mapperBuilderAssistant, Class<?> mapperClass) {
        String className = mapperClass.toString();
        Set<String> mapperRegistryCache = MybatisConfiguration.MAPPER_REGISTRY_CACHE;
        if (!mapperRegistryCache.contains(className)) {
            inject(configuration, mapperBuilderAssistant, mapperClass);
            mapperRegistryCache.add(className);
        }
    }


    /**
     * 注入单点SQL
     */
    @Override
    public void inject(Configuration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass) {
        System.out.println("执行sql语句注入方法");
        this.configuration = configuration;
        this.languageDriver = configuration.getDefaultScriptingLanuageInstance();
        this.builderAssistant = builderAssistant;
        this.dbType = MybatisConfiguration.DB_TYPE;


        Class<?> modelClass  = extractModelClass(mapperClass);
        TableInfo table = TableInfoHelper.initTableInfo(modelClass);

        /* 没有指定主键，默认方法不能使用(lcq: 没有主键，不给这个mapperClass增强) */
        if (null != table && null != table.getKeyProperty()) {

            /* 插入 */
            this.injectInsertOneSql(false, mapperClass, modelClass, table);
            this.injectInsertOneSql(true, mapperClass, modelClass, table);
            this.injectInsertBatchSql(mapperClass, modelClass, table);

            /* 删除 */
            this.injectDeleteSelectiveSql(mapperClass, modelClass, table);
            this.injectDeleteByMapSql(mapperClass, table);
            this.injectDeleteSql(false, mapperClass, modelClass, table);
            this.injectDeleteSql(true, mapperClass, modelClass, table);
            System.out.println("The modelClass is  (User ???)" + modelClass);

            /* 修改 */
            this.injectUpdateSql(false, mapperClass, modelClass, table);
            this.injectUpdateSql(true, mapperClass, modelClass, table);
            this.injectUpdateByIdSql(false, mapperClass, modelClass, table);
            this.injectUpdateByIdSql(true, mapperClass, modelClass, table);
            this.injectUpdateBatchById(mapperClass, modelClass, table);


            /* 查询 */
            this.injectSelectSql(false, mapperClass, modelClass, table);
            this.injectSelectSql(true, mapperClass, modelClass, table);
            this.injectSelectByMapSql(mapperClass, modelClass, table);
            this.injectSelectOneSql(mapperClass, modelClass, table);
            this.injectSelectCountSql(mapperClass, modelClass, table);
            this.injectSelectListSql(SqlMethod.SELECT_LIST, mapperClass, modelClass, table);
            this.injectSelectListSql(SqlMethod.SELECT_PAGE, mapperClass, modelClass, table);

            /* 自定义方法 */
            this.inject(configuration, builderAssistant, mapperClass, modelClass, table);

        } else {
            /*
             *  警告：主键属性未知，不进行mybatis-plus增强
             *  */
            logger.warning(String.format("%s, Not found @TableId annotation, cannot use the mybatis-plus method", mapperClass.toString()));
        }

    }

    public void inject (Configuration configuration, MapperBuilderAssistant builderAssistant,
                        Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        // to do nothing .
    }

    /**
     * 注入查询SQL语句 （主要是根据ID查询）
     * @param batch  是否批量查询
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectSelectSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_BY_ID;
        SqlSource sqlSource = null;
        String sql = null;
        if (batch) {
            sqlMethod = SqlMethod.SELECT_BATCH;
            StringBuilder ids = new StringBuilder();
            ids.append("\n<foreach collection=\"list\" item=\"item\" index=\"index\" separator=\",\">\n\t#{item}\n</foreach>");
            sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, false), table.getTableName(), table.getKeyColumn(), ids.toString());
            sqlSource = languageDriver.createSqlSource(configuration, sql, mapperClass);
        } else {
            sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, false), table.getTableName(), table.getKeyColumn(), table.getKeyProperty());
            sqlSource = new RawSqlSource(configuration, sql, mapperClass);
        }
        System.err.println("inject select(batch) sql: ");
        System.out.println( sql.replaceAll("\n", "") );
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * <p>注入 map 查询 SQL 语句</p>
     */
    protected void injectSelectByMapSql(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_BY_MAP;
        String sql = String.format(sqlMethod.getSql(),  sqlSelectColumns(table, false), table.getTableName(), sqlWhereByMap());
        System.err.println("inject select by map sql");
        System.out.println( sql.replaceAll("\n", "") );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * 注入实体查询 SQL 语句
     */
    protected void injectSelectOneSql (Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_ONE;
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, false), table.getTableName(), sqlWhere(table, false));
        System.err.println("inject select (one) sql("+ sqlMethod.getMethod() +")：");
        System.out.println( sql.replaceAll("\n", "") );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);
    }

    /**
     * <p>注入实体查询总记录数 SQL 语句</p>
     */
    protected void injectSelectCountSql (Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.SELECT_COUNT;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhere(table, true));
        System.err.println("inject select count sql : ");
        System.out.println( sql.replaceAll("\n", "") );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, Integer.class, null);
    }

    /**
     * <p>注入实体查询记录列表SQL语句</p>
     *
     * @param mapperClass       接口类型
     * @param modelClass          实体类型
     * @param table                     表
     */
    protected void injectSelectListSql (SqlMethod sqlMethod, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        StringBuilder where = new StringBuilder("\n<if test = \"ew != null\">");
        where.append("\n\t").append("<if test = \"ew.entity != null\">");

        where.append("\n\t\t").append("<where>");
        // # 主键         条件拼接
        where.append("\n\t\t\t").append("<if test = \"ew.entity.").append(table.getKeyProperty()).append("!= null\">");
        where.append("\t\t\t").append(table.getKeyColumn()).append("#{ew.entity.").append(table.getKeyProperty()).append("}");
        where .append("\t\t</if>");

        // # 对象属性  条件拼接
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            where.append("\n\t\t\t").append(convertIfTag(fieldInfo, "ew.entity", false));
            where.append("\t\t\t").append("  AND ").append(fieldInfo.getColumn()).append("=#{ew.entity.").append(fieldInfo.getEl()).append("}");
            where.append("\t\t").append(convertIfTag(fieldInfo, true));
        }

        where.append("\n\t\t").append("</where>");

        // # 拼接额外的 sql 片段 . 作用（条件过滤），同时也包含了 order by 的拼接
        where.append("\n<if test=\"ew.sqlSegment!=null\">\n${ew.sqlSegment}\n</if>");
        where .append("\n\t</if>");
        where .append("\n</if>");
        String sql = String.format(sqlMethod.getSql(), sqlSelectColumns(table, true), table.getTableName(), where.toString());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        System.err.println("inject select (list by ew) : ");
        System.out.println( sql.replaceAll("\n", "") );
        this.addSelectMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource, modelClass, table);

    }



    /**
     * <p>
     *     注入 insert sql语句
     * </p>
     *  <p>
     *     应用SQLMethod : INSERT_ONE or INSERT_ONE_SELECTIVE
     * </p>
     *
     * @param selective          是否选择插入（选择对象非空字段拼接insert sql）
     * @param mapperClass  Mapper Class对象
     * @param modelClass    MapperClass对应的实体类Class对象
     * @param table                表名
     */
    protected void injectInsertOneSql (boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {

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
        if (selective) {
            sqlMethod = SqlMethod.INSERT_ONE_SELECTIVE;
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
            fieldBuilder.append("\t").append(table.getKeyColumn()).append(",");
            placeholderBuilder.append("\t").append("#{").append(table.getKeyProperty()).append("},");
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

            if (selective) {
                // # 字段
                fieldBuilder
                        .append(convertIfTag(fieldInfo, false));
                // # 占位符
                placeholderBuilder
                        .append(convertIfTag(fieldInfo, false));
            }

            fieldBuilder.append(fieldInfo.getColumn()).append(",");
            placeholderBuilder.append("#{").append(fieldInfo.getEl()).append("},");

            if (selective) {
                fieldBuilder
                        .append(convertIfTag(fieldInfo, true));
                placeholderBuilder
                        .append(convertIfTag(fieldInfo, true));
            }

        }

        fieldBuilder.append("\n</trim>\n");
        placeholderBuilder.append("\n</trim>\n");

        String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(), placeholderBuilder.toString());
        System.err.println("inject insert(batch) sql: \n");
        System.out.println( sql.replaceAll("\n", "") );
        SqlSource sqlSource  = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator, keyProperty, keyColumn);
    }

    /**
     * 注入批量插入SQL语句
     * @param mapperClass mapperClass
     * @param modelClass modelClass
     * @param table table
     */
    protected void injectInsertBatchSql (Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        /*
        insert into table <trim prefix = "(" suffix=")" suffixOverrides=",">
        <if test = "xx != null">xx,</if>...
        </trim>
        */
        KeyGenerator keyGenerator = new NoKeyGenerator();
        StringBuilder fieldBuilder = new StringBuilder();
        StringBuilder placeholderBuilder = new StringBuilder();
        SqlMethod sqlMethod = SqlMethod.INSERT_BATCH_MYSQL;

        if (DBType.ORACLE == dbType) {
            sqlMethod = SqlMethod.INSERT_BATCH_ORACLE;
            placeholderBuilder.append("\n<trim prefix=\"(SELECT \" suffix=\" FROM DUAL)\" suffixOverrides=\",\">\n");
        } else {
            placeholderBuilder.append("\n<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        }
        fieldBuilder.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");

        String keyProperty = null;
        String keyColumn = null;
        if (table.getIdType() == IdType.AUTO) {
            /* 自增主键 */
            keyGenerator = new Jdbc3KeyGenerator();
            keyProperty = table.getKeyProperty();
            keyColumn = table.getKeyColumn();
        } else {
            /* 用户输入自定义ID */
            fieldBuilder.append(table.getKeyColumn()).append(",");
            placeholderBuilder.append("#{item.").append(table.getKeyProperty()).append("}").append(",");
        }
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            fieldBuilder.append(fieldInfo.getColumn()).append(",");
            placeholderBuilder.append("#{item.").append(fieldInfo.getEl()).append("}").append(",");
        }
        fieldBuilder.append("\n</trim>");
        placeholderBuilder.append("\n</trim>");
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), fieldBuilder.toString(), placeholderBuilder.toString());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        System.err.println("inject insert batch sql : ");
        System.out.println( sql.replaceAll("\n", "") );
        this.addInsertMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource, keyGenerator,
                keyProperty, keyColumn);
    }

    /**
     * <p>注入更新SQL语句</p>
     * @param selective          是否选择更新
     * @param mapperClass    mapperClass
     * @param modelClass      modelClass
     * @param table                table
     */
    protected void injectUpdateByIdSql (boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = (selective) ? SqlMethod.UPDATE_SELECTIVE_BY_ID : SqlMethod.UPDATE_BY_ID;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(selective, table), table.getKeyColumn(), table.getKeyProperty());
        System.err.println("\ninject "+ sqlMethod.getMethod() +" ("+ (selective ? "" : " not ") +"selective) sql ");
        System.out.println( sql.replaceAll("\n", "") );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * 注入 entity 条件删除 SQL 语句
     * @param mapperClass       mapperClass
     * @param modelClass        modelClass
     * @param table             table
     */
    protected void injectDeleteSelectiveSql (Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.DELETE_SELECTIVE;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhere(table, false));
        System.err.println("inject delete selective sql : ");
        System.out.println( sql.replaceAll("\n", "") );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * 删除by map
     * @param mapperClass mapperClass
     * @param table       table
     */
    protected void injectDeleteByMapSql(Class<?> mapperClass, TableInfo table) {
        SqlMethod sqlMethod = SqlMethod.DELETE_BY_MAP;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlWhereByMap());
        System.err.println("inject delete by map sql : ");
        System.out.println( sql.replaceAll("\n", "") );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, Map.class);
        this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * <p>注入批量更新 SQL 语句</p>
     */
    protected void injectUpdateBatchById(Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        StringBuilder set = new StringBuilder();
        set.append("<trim prefix=\"SET\" suffixOverrides=\",\">\n");
        SqlMethod sqlMethod = SqlMethod.UPDATE_BATCH_BY_ID_MYSQL;
        if (DBType.ORACLE == dbType) {
            sqlMethod = SqlMethod.UPDATE_BATCH_BY_ID_ORACLE;
            List<TableFieldInfo> fieldList = table.getFieldList();
            for (TableFieldInfo fieldInfo : fieldList) {
                set.append(fieldInfo.getColumn()).append("=#{item.").append(fieldInfo.getEl()).append("},");
            }
        } else if (DBType.MYSQL == dbType) {
            List<TableFieldInfo> fieldList = table.getFieldList();
            for (TableFieldInfo fieldInfo : fieldList) {
                set.append("\n<trim prefix=\"").append(fieldInfo.getColumn()).append("=CASE ");
                set.append(table.getKeyColumn()).append("\" suffix=\"END,\">");
                set.append("\n<foreach collection=\"list\" item=\"i\" index=\"index\">");
                set.append(convertIfTag(fieldInfo, "i.", false));
                set.append("\nWHEN ").append("#{i.").append(table.getKeyProperty());
                set.append("} THEN #{i.").append(fieldInfo.getEl()).append("}");
                set.append(convertIfTag(fieldInfo, true));
                set.append("\n</foreach>");
                set.append("\n</trim>");
            }
        }
        set.append("\n</trim>");

        String sql = String.format(sqlMethod.getSql(), table.getTableName(), set.toString(), table.getKeyColumn(),
                table.getKeyProperty());
        System.err.println("inject sql (updateBatchById) - "+ dbType.name()  + " : ");
        System.out.println( sql.replaceAll("\n", "") );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * 注入更新SQL语句，按实体非空成员条件更新.
     * @param selective
     * @param mapperClass
     * @param modelClass
     * @param table
     */
    protected void injectUpdateSql (boolean selective, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
        SqlMethod sqlMethod = (selective) ? SqlMethod.UPDATE_SELECTIVE : SqlMethod.UPDATE;
        String sql = String.format(sqlMethod.getSql(), table.getTableName(), sqlSet(selective, table), sqlWhere(table, true));
        System.err.println("inject update sql : ");
        System.out.println( sql.replaceAll("\n", "") );
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
    }

    /**
     * Sql更新语句set部分
     * @return
     */
    protected String sqlSet (boolean selective, TableInfo table) {
        /*
		 * UPDATE table
		 * <trim prefix="SET" suffixOverrides="," suffix="WHERE id=#{id}" >...</trim>
		 */
        StringBuilder set = new StringBuilder();
        set.append("<trim prefix=\"SET\" suffixOverrides=\",\">\n");

        set.append(table.getKeyColumn()).append("=#{et.").append(table.getKeyProperty()).append("}").append(",");

        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            if (selective) {
                set.append( convertIfTag(fieldInfo, "et.", false ));
            }
            set.append(fieldInfo.getColumn()).append("=#{et.").append(fieldInfo.getEl()).append("}").append(",");
            if (selective) {
                set.append( convertIfTag( fieldInfo, true ));
            }
        }
        set.append("\n</trim>");
        return set.toString();
    }

    /**
     * 注入删除 SQL 语句
     * @param batch  是否批量
     */
    protected void injectDeleteSql(boolean batch, Class<?> mapperClass, Class<?> modelClass, TableInfo table) {
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
        this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
    }



    protected MappedStatement addSelectMappedStatement(Class<?> mapperClass, String id, SqlSource sqlSource, Class<?> resultType, TableInfo table) {

        if (null != table) {
            String resultMap = table.getResultMap();
            if (null != resultMap) {
                /* 返回resultMap映射的结果集 */
                return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, resultMap, null, new NoKeyGenerator(), null, null);
            }
        }
        /* 普通查询 */
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.SELECT, null, null,
                resultType, new NoKeyGenerator(), null, null);
    }


    /**
     * 插入
     */
    public void addInsertMappedStatement(Class<?> mapperClass, Class<?> modelClass, String id, SqlSource source, KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        this.addMappedStatement(mapperClass, id, source, SqlCommandType.INSERT, modelClass, null, Integer.class, keyGenerator, keyProperty, keyColumn);
    }

    /**
     * 删除
     */
    public MappedStatement addDeleteMappedStatement (Class<?> mapperClass, String id, SqlSource sqlSource) {
        return this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.DELETE, null, null, Integer.class, new NoKeyGenerator(), null, null);
    }

    /**
     * 更新
     **/
    public void addUpdateMappedStatement (Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource) {
        this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null, Integer.class,
                new NoKeyGenerator(), null, null);
    }

    public MappedStatement addMappedStatement (Class<?> mapperClass, String id, SqlSource sqlSource, SqlCommandType sqlCommandType, Class<?> parameterClass, String resultMap, Class<?> resultType, KeyGenerator keyGenerator, String keyProperty, String keyColumn ) {

        String statementName = mapperClass.getName() + "." + id;
        if (configuration.hasStatement(statementName)) {
            System.err.println(statementName + ", Has been loaded by XML or SqlProvider, ignoring the injection of the SQL.");
            return null;
        }
        /* 缓存逻辑处理 */
        boolean isSelect = false;
        if (sqlCommandType == SqlCommandType.SELECT) {
            isSelect = true;
        }
        return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
                parameterClass, resultMap, resultType, null, !isSelect, isSelect, false, keyGenerator, keyProperty, keyColumn,
                configuration.getDatabaseId(), languageDriver, null);
    }

    /**
     * SQL 查询所有表字段，若为包装类型查询则允许查询部分字段
     * @param table   表名称
     * @param entityWrapper 是否为包装类型查询
     */
    protected String sqlSelectColumns (TableInfo table, boolean entityWrapper) {

        StringBuilder columns = new StringBuilder();

        // 存在 resultMap 映射返回 '*'
        if (null != table.getResultMap()) {
            if (entityWrapper) {
                columns.append("<choose><when test=\"ew != null and ew.sqlSelect != null\">${ew.sqlSelect}</when><otherwise>");
            }
            columns.append("*");
            if (entityWrapper) {
                columns.append("</otherwise></choose>");
            }
        } else {
            if (entityWrapper) {
                columns.append("<choose><when test=\"ew != null and ew.sqlSelect != null\">${ew.sqlSelect}</when><otherwise>");
            }
            // 普通查询
            // 返回所有查询字段内容
            // 获取主键,属性
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
            if (entityWrapper) {
                columns.append("</otherwise></choose>");
            }
        }


        /* 返回所有查询字段 */
        return columns.toString();
    }

    protected String sqlWhereByMap () {
        // # cm 是Map对象，keys代表 Map对象的键集合，foreach标签遍历到每个键，键名称代表 表字段名称
        StringBuilder where = new StringBuilder();
        where.append("\n<foreach collection=\"cm.keys\" item=\"k\" separator=\"AND\"> ");
        where.append("${k}=#{cm[${k}]}");
        where.append("\n</foreach>");
        return where.toString();
    }

    protected String convertIfTag (TableFieldInfo fieldInfo, String prefix, boolean close) {
        /* 前缀处理 */
        String property = fieldInfo.getProperty();
        if (null != prefix) {
            property = prefix + property;
        }

        /* 判断策略 */
        if (fieldInfo.getFieldStrategy() == FieldStrategy.NOT_NULL) {
            if (close) {
                return "</if>";
            } else {
                return String.format("\n\t<if test = \"%s != null\">", property);
            }
        } else if (fieldInfo.getFieldStrategy() == FieldStrategy.NOT_EMPTY) {
            if (close) {
                return "</if>";
            } else {
                return String.format("\n\t<if test = \"%s != null and %s != ''\">", property, property);
            }
        }
        return "";
    }

    protected String convertIfTag (TableFieldInfo fieldInfo, boolean close) {
        return convertIfTag(fieldInfo, null, close);
    }

    /**
     * SQL 查询条件
     * @param space 该变量标记是否拼接 ew 参数的空判断.
     */
    protected String sqlWhere (TableInfo table, boolean space) {
        StringBuilder where = new StringBuilder();
        if ( space ) {
            where.append("\n<if test=\"ew!=null\">");
        }
        where.append("\n<where>");
        // # 遍历成员属性
        // 主键成员
        where.append("\n\t<if test=\"ew.").append(table.getKeyProperty()).append(" != null\">");
        where.append("\t\t").append(table.getKeyColumn()).append(" = #{ew.").append(table.getKeyProperty()).append("}");
        where.append("</if>");
        // 其它成员
        List<TableFieldInfo> fieldList = table.getFieldList();
        for (TableFieldInfo fieldInfo : fieldList) {
            where.append(convertIfTag(fieldInfo, "ew.", false));
            where.append("\t\t\tAND  ").append(fieldInfo.getColumn()).append(" = #{ew.").append(fieldInfo.getEl()).append("}");
            where.append(convertIfTag(fieldInfo, true));
        }
        where.append("\n</where>");
        if ( space ) {
            where.append("\n</if>");
        }
        return where.toString();
    }

    protected Class<?> extractModelClass (Class<?> mapperClass) {
        // 返回这个类/接口中被直接实现的接口的类型（Types）。
        Type[] types = mapperClass.getGenericInterfaces();
        ParameterizedType target = null;
        for (Type type : types) {
            // 1. 有泛型参数 && 类型为AutoMapper.class
            if (type instanceof ParameterizedType && BaseMapper.class.isAssignableFrom(mapperClass) ) {
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