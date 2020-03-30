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

}
