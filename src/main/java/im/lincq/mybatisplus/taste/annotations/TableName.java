package im.lincq.mybatisplus.taste.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表名
 * @Date 20191021
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableName {
    /**表名 **/
    String value() default "";

    /**
     * 实体映射结果集
     */
    String resultMap() default "";

}
