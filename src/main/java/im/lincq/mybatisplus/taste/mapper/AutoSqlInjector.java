package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.annotation.Id;
import im.lincq.mybatisplus.taste.annotation.Table;
import im.lincq.mybatisplus.taste.toolkit.FieldReflectionHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;


public class AutoSqlInjector {
    private transient Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_ID = "id";

    // sql语句
    private static final String SQL_DELETE = "DELETE FROM %s WHERE %s = #{ID}";
    private static final String SQL_SELECTONE = "SELECT * FROM %s WHERE %s = #{ID}";
    private static final String SQL_SELECTALL = "SELECT * FROM FROM %s";

    private static final String METHOD_INSERTONE = "insert";
    private static final String METHOD_UPDATEONE = "updateById";
    private static final String METHOD_DELETEONE = "deleteById";
    private static final String METHOD_SELECTONE = "selectById";
    private static final String METHOD_SELECTALL= "selectAll";

    // mybatis配置类对象， 还有一个小助理
    private Configuration configuration;
    private MapperBuilderAssistant assistant;

    /**
     * 数据库主键
     */
    private TID tableID;

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
        String table = this.extractTableName(modelClass);
        tableID = this.extractTableID(modelClass);


        /* 新增 */
        this.injectInsertSql(mapperClass, modelClass, table);

        /* 没有指定主键，默认忽略主键修改，删除，查询方法 */
        if (tableID != null) {
            /* 根据主键修改，主键名默认未id */
            this.injectUpdateSql(mapperClass, modelClass, table);

            /* 根据主键删除，主键名默认未id */
            SqlSource sqlSource = new RawSqlSource(configuration, String.format(SQL_DELETE, table, tableID.name),
                    Object.class);
            this.addMappedStatement(mapperClass, METHOD_DELETEONE, sqlSource, SqlCommandType.DELETE, null);

            /*  根据主键查找，主键名默认为id*/
            sqlSource = new RawSqlSource(configuration, String.format(SQL_SELECTONE, table, tableID.name), Object.class);
            this.addMappedStatement(mapperClass, METHOD_SELECTONE, sqlSource, SqlCommandType.SELECT, modelClass);
        }
    }

    public void addMappedStatement (Class<?> mapperClass, String id, SqlSource sqlSource, SqlCommandType sqlCommandType, Class<?> resultType) {
        this.addMappedStatement(mapperClass, id, sqlSource, sqlCommandType, null, resultType, new NoKeyGenerator(), null, null);
    }

    /**
     * 注入 insert sql语句
     * @param mapperClass  Mapper Class对象
     * @param modelClass    MapperClass对应的实体类Class对象
     * @param table                表名
     */
    private void injectInsertSql (Class<?> mapperClass, Class<?> modelClass, String table) {
        KeyGenerator keyGenerator = new NoKeyGenerator();
        // 主键属性，主键字段
        String keyProperty = null;
        String keyColumn = null;

        List<Field> fields = FieldReflectionHelper.getAllFieldsExcludeTransient(modelClass);
        StringBuilder fieldBuilder = new StringBuilder();
        StringBuilder placeholderBuilder = new StringBuilder();

        int fieldSize = fields.size();
        for (int i=0; i<fieldSize; i++) {
            String fieldName = fields.get(i).getName();
            if (tableID != null && tableID.name.equals(fieldName) && tableID.auto) {
                keyGenerator = new Jdbc3KeyGenerator();
                keyProperty = keyColumn = tableID.name;
                continue;
            }
            fieldBuilder.append(fieldName);
            placeholderBuilder.append("#{").append(fieldName).append("}");
            if (i < fieldSize - 1) {
                fieldBuilder.append(",");
                placeholderBuilder.append(",");
            }
        }
        String sql = String.format("insert into %s(%s) values %s", table, fieldBuilder.toString(), placeholderBuilder.toString());
        SqlSource sqlSource  = new RawSqlSource(configuration, sql, modelClass);
        this.addInsertMappedStatement(mapperClass, modelClass, METHOD_INSERTONE, sqlSource, keyGenerator, keyProperty, keyColumn);
    }

    private void injectUpdateSql (Class<?> mapperClass, Class<?> modelClass, String table) {
        List<Field> fields = FieldReflectionHelper.getAllFieldsExcludeTransient(modelClass);
        StringBuilder sqlBuilder = new StringBuilder("UPDATE ").append(table).append(" SET ");
        int fieldSize = fields.size();
        for (int i = 0; i < fieldSize; i++) {
            String fieldName = fields.get(i).getName();
            // 跳过主键属性
            if (tableID.name.equals(table)) {
                continue;
            }
            sqlBuilder.append(fieldName).append("=#{").append(fieldName).append("}");
            if (i < fieldSize - 1) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(" WHERE ").append(tableID.name).append("=#{").append(tableID.name).append("}");
        SqlSource sqlSource = new RawSqlSource(configuration, sqlBuilder.toString(), mapperClass);
        this.addUpdateMappedStatement(mapperClass, modelClass, METHOD_UPDATEONE, sqlSource);
    }

    private void addUpdateMappedStatement (Class<?> mapperClass, Class<?> modelClass, String id, SqlSource sqlSource) {
        this.addMappedStatement(mapperClass, id, sqlSource, SqlCommandType.UPDATE, modelClass, null,
                new NoKeyGenerator(), null, null);
    }

    private void addMappedStatement (Class<?> mapperClass, String id, SqlSource sqlSource, SqlCommandType sqlCommandType,
                                     Class<?> parameterClass, Class<?> resultType, KeyGenerator keyGenerator, String keyProperty, String keyColumn ) {
        String statementName = mapperClass.getName() + "." + id;
        if (configuration.hasStatement(statementName)) {
            logger.warn("{},已通过xml或SqlProvider加载了，忽略该sql的注入", statementName);
            return;
        }
        assistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType, null, null, null,
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
            if (type instanceof ParameterizedType && ((ParameterizedType)type).getRawType().equals(AutoMapper.class) ) {
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

    /***
     * 功能：提取数据库表名
     */
    private String extractTableName (Class<?> modelClass) {
        Table tableAnno = modelClass.getAnnotation(Table.class);
        if (tableAnno != null && tableAnno.name().trim().length() > 0) {
            return tableAnno.name();
        }
        return this.camelToUnderline(modelClass.getSimpleName());
    }

    /**
     * 提取主键id <br></br>
     * 对象身上所有的属性中，抽取出TID对象，TID对象描述modelClass中主键主键的属性名称，是否自增. <br></br>
     * 首先，成员变量身上如果有id注解，可以说明该成员变量对应主键.即：成员变量名称是主键名称，而id主键auto属性值为是否自增值  <br></br>
     * 如成员变量们都没有id注解，则mybatis-plus默认名为id，类型为long/Long或int/Integer类型的成员属性对应自增主键  <br></br>
     * @param modelClass    实体类Class 对象
     * @return                           TID
     */
    private TID extractTableID (Class<?> modelClass) {
        List<Field> fields = FieldReflectionHelper.getAllFieldsExcludeTransient(modelClass);
        TID tId = null;
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                tId = new TID();
                tId.name = field.getName();
                tId.auto = id.auto();
                break;
            }
        }
        if (tId != null) {
            return tId;
        }
        /* 检测是否采用了默认id，作为主键 */
        for (Field field : fields) {
            if (this.isDefaultAutoID(field)) {
                tId = new TID();
                tId.name = field.getName();
                tId.auto = true;
                return tId;
            }
        }
        return null;
    }

    /**
     * 判定是否为自增主键id.
     * @param field 字段属性
     * @return          判断结果
     */
    public boolean isDefaultAutoID (Field field) {
        String fieldName = field.getName();
        if (DEFAULT_ID.equals(fieldName)) {
            // 如果id属性的类型是Long/long/Integer/int类型则判断对象是自增类型
            Class<?> fieldClass = field.getType();
            if (fieldClass.equals(Long.class) || fieldClass.equals(long.class) || fieldClass.equals(Integer.class) || fieldClass.equals(int.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 功能：驼峰单词转下划线风格
     * @param camelWord 驼峰单词
     * @return                        下划线风格字符串
     */
    private String camelToUnderline (String camelWord) {
        if (camelWord == null || camelWord.length() == 0) {
            return "";
        }
        int len = camelWord.length();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<len; i++) {
            char c = camelWord.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    /**
     * Table ID
     */
    class TID {
        // id主键属性名称 [是否自增]
        String name;
        boolean auto;
    }
}
