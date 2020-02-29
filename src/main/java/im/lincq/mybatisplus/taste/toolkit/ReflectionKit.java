package im.lincq.mybatisplus.taste.toolkit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * 调用对象的get方法检查对象所有属性是否为 null
     * 注意：只要有一个Field的值不为空，方法就可以返回TRUE
     *
     * @param bean 对象实例
     * @return boolean true 对象所有属性不为null, false对象所有属性为null
     */
    public static boolean checkFieldValueNull (Object bean) {
        boolean result = false;
        if (bean == null) {
            return true;
        }
        Class<?> cls = bean.getClass();
        Method[] methods = cls.getDeclaredMethods();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);

        if (tableInfo == null) {
            logger.warn("Warn: Could not find @TableId.");
            return false;
        }
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        for (TableFieldInfo tableFieldInfo : fieldList) {
            String fieldGetName = StringUtils.concatCapitalize("get", tableFieldInfo.getProperty());
            if (!checkMethod(methods, fieldGetName)) {
                continue;
            }
            try {
                Method method = cls.getMethod(fieldGetName);
                Object obj = method.invoke(bean);
                if (null != obj) {
                    result = true;
                    break;
                }
            } catch (Exception e) {
                logger.warn("Warn: Unexpected exception on checkFieldValueNull. Cause:" + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 判断是否存在某属性的get方法 （判断方法不存在）
     * @param methods  对象所有的方法
     * @param method   当前检查的方法
     * @return boolean  true存在， false不存在
     */
    public static boolean checkMethod (Method[] methods, String method) {
        for (Method met : methods) {
            if (method.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

}
