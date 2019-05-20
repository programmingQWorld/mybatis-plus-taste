package im.lincq.mybatisplus.taste.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>数据库主键标识</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableId {

    /**
     * 字段值（驼峰命名方式，该值可无）
     */
    String value() default "";


    /**
     * 主键ID，默认 ID 自增
     * {@link im.lincq.mybatisplus.taste.annotations.IdType}
     */
    IdType type() default IdType.AUTO_INCREMENT;
}
