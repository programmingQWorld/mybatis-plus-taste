package im.lincq.mybatisplus.taste.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 表字段标识
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TableField {

    /**
	 * 
	 * <p>
	 * 是否为数据库表字段
	 * </p>
	 * 默认 true 存在，false 不存在
	 * 
	 */
	boolean exist() default true;

}
