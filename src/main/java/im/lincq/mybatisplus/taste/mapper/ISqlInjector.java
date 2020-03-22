package im.lincq.mybatisplus.taste.mapper;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

/**
 *<p>SQL 自动注入器接口</p>
 */
public interface ISqlInjector {

    /**
     *<p>注入SQL</p>
     */
    void inject(Configuration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass);

    /**
     * <p>
     * 检查SQL是否已经注入
     * ps:注入基本SQL后给予标识 注入过不再注入
     * </p>
     */
    void inspectInject(Configuration configuration, MapperBuilderAssistant builderAssistant, Class<?> mapperClass);

}
