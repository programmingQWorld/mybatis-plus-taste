package im.lincq.mybatisplus.taste.toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


/**
 * 反射工具类.
 * @authors: lincq
 * @date: 2020/2/29 20:44
 **/
public class ReflectionKit {

    private static Logger logger = LoggerFactory.getLogger(ReflectionKit.class);

    /**
     * 反射 method 方法名，例如 getId
     * @param fieldName  类成员名称
     * @return  get+(类成员名称（首字母大写）)
     */
    public static String getMethodCapitalize (final String fieldName) {
        // (get, name) → getName
        return StringUtils.concatCapitalize("get", fieldName);
    }

    /**
     * 获取 public get 方法的值
     * @param cls            实体类
     * @param entity         实体类实例
     * @param keyProperty    属性字符串内容
     * @return      Object
     */
    public static Object getMethodValue(Class<?> cls, Object entity, String keyProperty) {
        Object obj = null;
        try {
            Method method = cls.getMethod(ReflectionKit.getMethodCapitalize(keyProperty));
            obj = method.invoke(entity);
        } catch (NoSuchMethodException e) {
            logger.warn("Warn: No such method. in " + cls);
        } catch (IllegalAccessException e) {
            logger.warn("Warn: Cannot execute a private method. in" + cls);
        } catch (InvocationTargetException e) {
            logger.warn("Warn: Unexpected exception on getMethodValue. Cause:" + e);
        }
        return obj;
    }

    /**
     * 获取 public get方法的值
     *
     * @param entity 实体
     * @param str    属性字符串内容
     * @return Object
     */
    public static Object getMethodValue(Object entity, String str) {
        return null != entity
                ? getMethodValue(entity.getClass(), entity, str)
                : null;
    }

    /**
     * 调用对象的get方法检查对象所有属性是否为 null
     * 注意：只要有一个Field的值不为空，方法就可以返回TRUE
     *
     * @param bean 对象实例
     * @return boolean true 对象所有属性不为null, false对象所有属性为null
     */
    public static boolean checkFieldValueNotNull (Object bean) {
        if (null == bean) {
            return false;
        }

        Class<?> cls = bean.getClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);

        if (null == tableInfo) {
            logger.warn("Warn: Could not find @TableId.");
            return false;
        }

        boolean result = false;
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        for (TableFieldInfo tableFieldInfo : fieldList) {
            Object methodValue = getMethodValue(cls, bean, tableFieldInfo.getProperty());
            if (null != methodValue) {
                result = true;
                break;
            }
        }
        return result;
    }

}
