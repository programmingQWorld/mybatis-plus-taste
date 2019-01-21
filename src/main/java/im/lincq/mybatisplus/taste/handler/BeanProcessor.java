package im.lincq.mybatisplus.taste.handler;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/**
 * @author cover by lin-cq
 */
public class BeanProcessor {
    private static final int PROPERTY_NOT_FOUND = -1;
    private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<>();

    static {
        primitiveDefaults.put(Short.TYPE, Short.valueOf((short) 0));
        primitiveDefaults.put(Byte.TYPE, Byte.valueOf((byte) 0));
        primitiveDefaults.put(Integer.TYPE, Integer.valueOf(0));
        primitiveDefaults.put(Float.TYPE, Float.valueOf(0f));
        primitiveDefaults.put(Double.TYPE, Double.valueOf(0d));
        primitiveDefaults.put(Long.TYPE, Long.valueOf(0L));
        primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
        primitiveDefaults.put(Character.TYPE, Character.valueOf((char) 0));
    }

    public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {
        // 获取转换目标类属性
        // 获取表的列字段信息
        // 创建实体类实例
        PropertyDescriptor[] props = this.propertyDescriptor(type);
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int[] columnToProperty = this.mapColumnsToProperties(resultSetMetaData, props);
        return this.createBean(rs, type, props, columnToProperty);
    }

    private <T> T createBean(ResultSet rs, Class<T> type,
                             PropertyDescriptor[] props, int[] columnToProperty)
            throws SQLException {
        T bean = this.newInstance(type);
        // 这个地方也配合了mapColumnsToProperties方法中columnToProperty数组多一个长度的写法
        for (int i = 1; i < columnToProperty.length; i++) {
            if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
                continue;
            }
            PropertyDescriptor prop = props[columnToProperty[i]];
            // 变量类型
            Class<?> propType = prop.getPropertyType();
            // 获取到属性值
            Object value = this.processColumn(rs, i, propType);

            if (propType != null && value == null && propType.isPrimitive()) {
                value = primitiveDefaults.get(propType);
            }
            // 设置成员属性的值
            this.callSetter(bean, prop, value);
        }
        return bean;
    }

    /**
     * 功能：调用Setter方法，为类的各个属性复制.
     *
     * @param target 设置值的到target对象上.
     * @param prop   类的属性.可从中获取到get/set方法的方法对象
     * @param value  将被设置到属性的具体值
     * @throws SQLException
     */
    private void callSetter(Object target, PropertyDescriptor prop, Object value)
            throws SQLException {
        Method setter = prop.getWriteMethod();
        if (setter == null) {
            return;
        }

        // 接下来的动作篇幅会比较大，也是与对象类型相关
        // 做日期时间，枚举类型的判断.

        // setter方法的参数类型列表
        Class<?>[] params = setter.getParameterTypes();
        try {
            if (value instanceof Date) {
                // 为什么会是第一个参数..-->
                // set value给一个对象属性，一般只有一个值对应一个参数.
                // [0]index的目的应该就只是拿到setter方法中的参数类型
                final String dateType = params[0].getName();
                if ("java.sql.Date".equals(dateType)) {
                    value = new java.sql.Date(
                            ((Date) value).getTime());
                } else if ("java.sql.Time".equals(dateType)) {
                    value = new Time(
                            ((Date) value).getTime());
                } else if ("java.sql.TimeStamp".equals(dateType)) {
                    value = new Timestamp(
                            ((Date) value).getTime());
                }
            }
            if (params[0].isEnum()) {
                // hack oracle
                if (BigDecimal.class.isInstance(value)) {
                    value = ((BigDecimal) value).intValue();
                }
                final Class<?> enumType = params[0];
                if (String.class.isInstance(value)) {
                    value = valueOf(enumType, value);
                } else if (Integer.class.isInstance(value)) {
                    value = valueOf(enumType, value);
                }
            }
            if (this.isCompatibleType(value, params[0])) {
                setter.invoke(target, new Object[]{value});
            } else {
                // 类型不兼容，设置不了值了，抛出异常.
                throw new SQLException("Cannot set " + prop.getName()
                        + ": incompatible types, cannot convert )"
                        + value.getClass().getName() + " to "
                        + params[0].getName());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            // 若上面抛出异常，会将异常信息接获在这里合并异常信息后继续抛出
            throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
        }
    }

    private Enum<?> valueOf(Class<?> enumType, Object value)
        throws SQLException {
        try {
            Enum<?>[] elements = (Enum<?>[])enumType.getMethod("values")
                    .invoke(enumType);

            boolean ordinal = String.valueOf(elements[0].ordinal()).equals(
                    elements[0].toString());
            // TODO: 写下ordinal与name的区别，已知他们都是Enum类的属性.
            String readMethodName = ordinal ? "ordinal" : "name";

            if (ordinal && String.class.isInstance(value)) {
                value = Integer.parseInt(String.valueOf(value));
            }

            // 如果值是枚举的某一个类型,找到值是枚举中的哪一个元素变量
            Method readMethod = enumType.getMethod(readMethodName);
            for (Enum<?> element : elements) {
                if (readMethod.invoke(element).equals(value)) {
                    return element;
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 功能：检查值是否有类型对应
     * @param value     值，来源于数据库
     * @param type      类型对象
     * @return          能对应：true, 不能对应：false.
     */
    private boolean isCompatibleType(Object value, Class<?> type) {
        if (value == null || type.isInstance(value)) {
            return true;

        } else if (type.equals(Integer.TYPE) && Integer.class.isInstance(value)) {
            return true;

        } else if (type.equals(Long.TYPE) && Long.class.isInstance(value)) {
            return true;

        } else if (type.equals(Double.TYPE) && Double.class.isInstance(value)) {
            return true;

        } else if (type.equals(Float.TYPE) && Float.class.isInstance(value)) {
            return true;

        } else if (type.equals(Short.TYPE) && Short.class.isInstance(value)) {
            return true;

        } else if (type.equals(Byte.TYPE) && Byte.class.isInstance(value)) {
            return true;

        } else if (type.equals(Character.TYPE)
                && Character.class.isInstance(value)) {
            return true;

        } else if (type.equals(Boolean.TYPE) && Boolean.class.isInstance(value)) {
            return true;

        } else if (type.isEnum() && Integer.class.isInstance(value)) {
            return true;
        } else if (type.isEnum() && String.class.isInstance(value)) {
            return true;
        }
        return false;
    }

    /**
     * 功能：返回在结果集中与指定属性的对应类型的值
     * 处理：手动判断不同类型应当使用ResultSet对象的getXXX方法来获取值.
     *
     * @param rs       结果集
     * @param index    下标.类属性数组中的索引
     * @param propType 属性的类型类对象.(基本数据类型 | 引用类型)
     * @return
     */
    private Object processColumn(ResultSet rs, int index, Class<?> propType)
            throws SQLException {

        if (!propType.isPrimitive() && rs.getObject(index) == null) {
            return null;
        }
        if (propType.equals(String.class)) {
            return rs.getString(index);

        } else if (propType.equals(Integer.TYPE) || propType.equals(Integer.class)) {
            return rs.getInt(index);

        } else if (propType.equals(Boolean.TYPE) || propType.equals(Boolean.class)) {
            return rs.getBoolean(index);

        } else if (propType.equals(Long.TYPE) || propType.equals(Long.class)) {
            return rs.getLong(index);

        } else if (propType.equals(Double.TYPE) || propType.equals(Double.class)) {
            return rs.getDouble(index);

        } else if (propType.equals(Float.TYPE) || propType.equals(Float.class)) {
            return rs.getFloat(index);

        } else if (propType.equals(Short.TYPE) || propType.equals(Short.class)) {
            return rs.getShort(index);

        } else if (propType.equals(Byte.TYPE) || propType.equals(Byte.class)) {
            return rs.getByte(index);

        } else if (propType.equals(Timestamp.class)) {
            return rs.getTimestamp(index);

        } else if (propType.equals(Date.class)) {
            return rs.getDate(index);

        } else if (propType.equals(SQLXML.class)) {
            return rs.getSQLXML(index);

        } else {
            return rs.getObject(index);

        }

    }

    /**
     * 个人觉得作者将构建实例的代码抽出来方法中写，
     * 可能是因为要处理的异常偏多，影响原主干的篇幅.
     *
     * @param c   类Class对象
     * @param <T> 泛型指定运行时类型
     * @return 该类的实例对象
     * @throws SQLException 若创建实体对象失败..抛出此异常
     */
    private <T> T newInstance(Class<T> c) throws SQLException {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SQLException("Cannot create " + c.getName() + ": " + e.getMessage());
        }
    }

    /**
     * @param resultSetMetaData ResultSetMetaData，可从中获取数据表中的列信息
     * @param props             一个类的属性集合，可从中获取到get,set方法的方法对象
     * @return array:属性的顺序.
     */
    private int[] mapColumnsToProperties(ResultSetMetaData resultSetMetaData,
                                         PropertyDescriptor[] props) throws SQLException {
        int cols = resultSetMetaData.getColumnCount();

        // cols已经记录到位了，为什么要再多一个空间？打一个记录输出，应该能够了解
        // 2019.01.18 - 因为这里是resultSetMetaData获取的fields为3，而PropertyDescriptor的长度为4. propertyDescriptor比fields多一个属性：class属性.
        System.out.println("辅助了解为什么会有多的一个空间长度." + cols);
        int[] columnToProperty = new int[cols + 1];
        // 创建属性数组并初始化值为-1，代表未找到属性.
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        // 类属性名称的小写形式作为key.并分配一个数值作为value.
        Map<String, Integer> propertyMap = new HashMap<>(17);
        for (int i = 0; i < props.length; i++) {
            propertyMap.put(props[i].getName().toLowerCase(), i);
        }

        for (int col = 1; col <= cols; col++) {
            // ResultSetMetaData#getColumnName(第一列从1开始)
            // 先从 as 列别名开始获取，如果列别名没有则从字段名称中获取.个人觉得if判断是没有意义的.
            // 取到列名称之后，将列名称去掉下划线并转为小写形式.
            // 此时，类属性名称与结果集中的字段名称.在这里的风格一致（均为无下划线，无大写英文字母）
            String columnName = resultSetMetaData.getColumnLabel(col);
            if (null == columnName || 0 == columnName.length()) {
                columnName = resultSetMetaData.getColumnName(col);
            }
            String propertyName = columnName.replaceAll("_", "").toLowerCase();
            if (propertyMap.containsKey(propertyName)) {
                columnToProperty[col] = propertyMap.get(propertyName);
            }
        }
        return columnToProperty;
    }

    /**
     * 获取Java类的属性信息
     *
     * @param c
     * @return
     * @throws SQLException
     */
    private PropertyDescriptor[] propertyDescriptor(Class<?> c)
            throws SQLException {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(c);
        } catch (IntrospectionException e) {
            throw new SQLException("Bean introspection failed: " + e.getMessage());
        }
        return beanInfo.getPropertyDescriptors();
    }

    public static void main(String[] args) {
        String s1 = "Method";
        String s2 = "tdhoMe";

        HashMap<Character, Integer> map = new HashMap(6);
        int[] cols = new int[s2.length()];
        for (int i = 0; i < s1.length(); i++) {
            map.put(s1.charAt(i), i);
        }
        for (int i = 0; i < cols.length; i++) {
            char key = s2.charAt(i);
            cols[i] = map.get(key);
        }
        for (int col : cols) {
            System.out.print(s1.charAt(col));
        }
    }
}
