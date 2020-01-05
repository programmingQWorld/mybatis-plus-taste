package im.lincq.mybatisplus.taste.annotations;

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
	 * 字段值（驼峰命名方式，该值可无）
	 */
	String value() default "";


	/**
	 * 
	 * <p>
	 * 是否为数据库表字段
	 * </p>
	 * 默认 true 存在，false 不存在
	 * 
	 */
	boolean exist() default true;

	/**
	 * 当该Field为类对象时, 可使用#{对象.属性}来映射到数据表.
	 */
	String el() default "";

}
