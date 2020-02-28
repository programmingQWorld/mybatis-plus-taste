package im.lincq.mybatisplus.taste.toolkit;

import java.util.Collection;

/**
 * Collection工具类
 * @authors: lincq
 * @date: 2020/2/28 17:18
 **/
public class CollectionUtil {

    /**
     * 校验集合是否为空
     * @param coll 集合对象
     * @return      boolean
     */
    public static boolean isEmpty (Collection<?> coll) {
        return ((coll == null) || coll.isEmpty());
    }

    /**
     * 校验集合是否不为空
     * @param coll 集合对象
     * @return      boolean
     */
    public static boolean isNotEmpty (Collection<?> coll) {
        return !isEmpty(coll);
    }

}
