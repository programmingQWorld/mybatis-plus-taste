package im.lincq.mybatisplus.taste.test.mysql;

import im.lincq.mybatisplus.taste.MybatisMetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

/**
 * @author lincq
 * @date 2019/8/29 15:11
 */
public class MyMetaObjectHandler implements MybatisMetaObjectHandler {

    /**
     * 测试user表name字段为空自动填充
     */
    public void insertFill (MetaObject metaObject) {
        Object name = metaObject.getValue("name");
        if (null == name) {
            metaObject.setValue("name", "insert-fill");
            metaObject.setValue("testType", 2);
        }
    }

}
