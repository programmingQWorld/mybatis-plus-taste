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
     * 主键ID，默认true标记数据库主键自增
     * <p>
     *     若设置为false,需要用户传入ID内容，工具包IdWorker可产生全局唯一 ID
     * </p>
     * {@link im.lincq.mybatisplus.taste.toolkit.IdWorker}
     * @return
     */
    boolean auto() default true;
}
