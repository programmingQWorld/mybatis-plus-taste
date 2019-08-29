package im.lincq.mybatisplus.taste;

import org.apache.ibatis.reflection.MetaObject;

/**
 * <p>元对象字段填充控制器抽象接口，实现公共字段自动写入</p>
 * @author lincq
 * @date 2019/8/29 15:32
 */
public interface MybatisMetaObjectHandler {

    /**
     * <p>
     *     插入元对象字段填充
     * </p>
     * @param metaObject 元对象
     */
    void insertFill (MetaObject metaObject);
}
