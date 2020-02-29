package im.lincq.mybatisplus.taste.toolkit;

import im.lincq.mybatisplus.taste.MybatisConfiguration;
import im.lincq.mybatisplus.taste.annotations.TableField;
import im.lincq.mybatisplus.taste.annotations.TableId;
import im.lincq.mybatisplus.taste.annotations.TableName;
import im.lincq.mybatisplus.taste.exceptions.MybatisPlusException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 实体类反射表辅助类
 * </p>
 * @Date 2019-03-03
 */
public class TableInfoHelper {
    /**
     * 缓存反射类表信息
     */
    private static Map<String, TableInfo> tableInfoCache = new ConcurrentHashMap<>();

    /**
     * 根据实体类反射获取表信息
     * @param clazz  反射对象
     * @return            tableInfo
     */
    public static TableInfo getTableInfo(Class<?> clazz) {
        return tableInfoCache.get(clazz.getName());
    }

    /**
     *实体类反射获取表信息【初始化】
     * @param clazz  反射实体类
     * @return 表信息
     */
    public synchronized static TableInfo initTableInfo(Class<?> clazz) {
        TableInfo ti = tableInfoCache.get(clazz.getName());
        if (ti != null) {
            return ti;
        }
        TableInfo tableInfo = new TableInfo();

        /* 表名 */
        TableName table = clazz.getAnnotation(TableName.class);
        if (table != null && StringUtils.isNotEmpty(table.value())) {
            tableInfo.setTableName(table.value());
        } else {
            tableInfo.setTableName(StringUtils.camelToUnderline(clazz.getSimpleName()));
        }
        /* 表结果集映射 */
        if (table != null && StringUtils.isNotEmpty(table.resultMap())) {
            tableInfo.setResultMap(table.resultMap());
        }
        List<TableFieldInfo> fieldList = new ArrayList<>();
        List<Field> list = getAllFields(clazz);
        for (Field field : list) {
            /* 主键ID */
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null) {
                if (tableInfo.getKeyColumn() == null) {
                    tableInfo.setIdType(tableId.type());
                    if (StringUtils.isNotEmpty(tableId.value())) {
                        /*主键字段名称可能会和当前属性名称不一样，plus遵循当前的注解value配置主键字段名称*/
                        tableInfo.setKeyColumn(tableId.value());
                        tableInfo.setKeyRelated(true);
                    } else if (MybatisConfiguration.DB_COLUMN_UNDERLINE)  {
                        /* 开启字段下划线声明 */
                        tableInfo.setKeyColumn(StringUtils.camelToUnderline(field.getName()));
                    } else {
                        tableInfo.setKeyColumn(field.getName());
                    }
                    tableInfo.setKeyProperty(field.getName());
                    continue;

                } else {
                    /* 发现设置多个主键主键，抛出异常 */
                    throw new MybatisPlusException("There must be only one, Discover multiple @TableId annotation in " + clazz);
                }
            }
            /* 字段 也支持通过注解自定义映射表字段 */
            TableField tableField = field.getAnnotation(TableField.class);

            if (tableField != null) {
                /* TableFieldInfo 第二个参数,
                   前面一直认为这里需要对column字段进行驼峰转下划线格式才行,
                   现在想来是不用的,在注解上完成正确的字段名称填写即可.
                   在后面的版本中,好像也是按照这样的方式进行的,无需转换就不用做处理,
                   属性名和字段名对应不上就在注解上填写正确的字段名称.
                   */

                String columnName = field.getName();
                if (StringUtils.isNotEmpty(tableField.value())) {
                    columnName = tableField.value();
                }
                /*
                el语法支持，可以传入多个参数，以逗号隔开
                 * */
                String el = field.getName();
				if (StringUtils.isNotEmpty(tableField.el())) {
					el = tableField.el();
                }


                String[] columns = columnName.split(";");
                String[] els = el.split(";");

                if (columns.length == els.length) {
                    for (int i = 0; i < columns.length; i++) {
                        fieldList.add(new TableFieldInfo(true, columns[i], field.getName(), els[i], tableField.validate()));
                    }
                } else {
                    String errMsg = "Class %s, Field %s, 'value' 'el' length must be consistent.";
                    throw new MybatisPlusException(String.format(errMsg, clazz.getName(), field.getName()));
                }
                continue;
            }

            /*
			 * 字段, 使用 camelToUnderline 转换驼峰写法为下划线分割法, 如果已指定 TableField, 便不会执行这里
			 */
            if (MybatisConfiguration.DB_COLUMN_UNDERLINE) {
                fieldList.add(new TableFieldInfo(true, StringUtils.camelToUnderline(field.getName()), field.getName()));
            } else {
                /* 不需要自定义映射表字段  会影响到是否追加 as 部分*/
                fieldList.add(new TableFieldInfo( field.getName() ));
            }

        }

        /* 字段列表 */
        tableInfo.setFieldList(fieldList);

        /* 未发现主键注解，在 SqlInjector 中调用 InitTableInfo ，在这里得到  null , 跳过注入 */
        if (null == tableInfo.getKeyColumn()) {
            return null;
        }

        /* 执行注入，将表对象信息缓存起来，之后还可以继续使用 */
      tableInfoCache.put(clazz.getName(), tableInfo);
      return tableInfo;
    }

    /**
     * 获取该类的所有属性列表
     * @param clazz 反射对象
     * @return            List<Field>
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> result = new LinkedList<>();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            /* 过滤 transient关键字修饰的属性 */
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            /* 过滤注解非表字段属性 */
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField == null || tableField.exist()) {
                result.add(field);
            }
        }

        /* 处理父类字段 */
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(Object.class)) {
            return result;
        }
        result.addAll(getAllFields(superClass));
        return result;
    }

}
