package im.lincq.mybatisplus.taste.toolkit;

import im.lincq.mybatisplus.taste.annotations.TableField;
import im.lincq.mybatisplus.taste.annotations.TableId;
import im.lincq.mybatisplus.taste.annotations.TableName;

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
    private static Map<String, TableInfo> tableInfoCache = new ConcurrentHashMap<String, TableInfo>();

    /**
     * 根据实体类反射获取表信息
     * @param clazz  反射对象
     * @return            tableInfo
     */
    public synchronized static TableInfo getTableInfo(Class<?> clazz) {
        TableInfo ti = tableInfoCache.get(clazz.getName());
        if (ti != null) {
            return ti;
        }
        List<Field> list = getAllFields(clazz);
        TableInfo tableInfo = new TableInfo();

        /* 表名 */
        TableName table = clazz.getAnnotation(TableName.class);
        if (table != null && table.value().trim().length() > 0) {
            tableInfo.setTableName(table.value());
        } else {
            tableInfo.setTableName(camelToUnderline(clazz.getSimpleName()));
        }

        List<TableFieldInfo> fieldList = new ArrayList<TableFieldInfo>();
        for (Field field : list) {
            /* 主键ID */
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null) {
                tableInfo.setAutoIncrement(tableId.auto());
                if (!"".equals(tableId.value())) {
                    /*主键字段名称可能会和当前属性名称不一样，plus遵循当前的注解value配置主键字段名称*/
                    tableInfo.setKeyColumn(tableId.value());
                }
                tableInfo.setKeyProperty(field.getName());
                continue;
            }
            /* 字段 也支持通过注解自定义映射表字段 */
            TableField tableField = field.getAnnotation(TableField.class);

            if (!"".equals(tableField.value())) {
                /* TableFieldInfo 第二个参数,
                   前面一直认为这里需要对column字段进行驼峰转下划线格式才行,
                   现在想来是不用的,在注解上完成正确的字段名称填写即可.
                   在后面的版本中,好像也是按照这样的方式进行的,无需转换就不用做处理,
                   属性名和字段名对应不上就在注解上填写正确的字段名称.
                   */
                fieldList.add(new TableFieldInfo(true, tableField.value(), field.getName()));
                continue;
            }

            /* 不需要自定义映射表字段 */
            fieldList.add(new TableFieldInfo( field.getName() ));
        }

        /* 字段列表 */
        tableInfo.setFieldList(fieldList);
        tableInfoCache.put(clazz.getName(), tableInfo);
        return tableInfo;
    }

    private static String camelToUnderline(String param) {
        if (param == null || "".equals(param)) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i=0; i<len; i++) {
            char c = param.charAt(i);
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
     * 获取该类的所有属性列表
     * @param clazz 反射对象
     * @return            List<Field>
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> result = new LinkedList<Field>();
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
