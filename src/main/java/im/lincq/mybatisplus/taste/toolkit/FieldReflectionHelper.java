package im.lincq.mybatisplus.taste.toolkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段反射辅助类
 */
public class FieldReflectionHelper {

    public static List<Field> getAllFields (Class<?> clazz) {
        List<Field> result = new LinkedList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            result.add(field);
        }
        // 可取代上面for循环写法: Collections.addAll(result, fields);
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(Object.class)) {
            return result;
        }
        result.addAll(getAllFields(superClass));
        return result;
    }
    /**
     * 获取该类的所有字段列表，排查transient修饰的字段属性.
     * @param clazz     反射类
     * @return                字段属性列表
     */
    public static List<Field> getAllFieldsExcludeTransient (Class<?> clazz) {
        List<Field> result = new LinkedList<Field>();
        List<Field> list = getAllFields(clazz);
        for (Field field : list) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            result.add(field);
        }
        // 用stream流式操作数据就会比较好.
        //return list.stream().filter(field -> Modifier.isTransient(field.getModifiers())).collect(Collectors.toList());
        return result;
    }
}
