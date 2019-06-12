package im.lincq.mybatisplus.taste;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;

/**
 * <p>
 *     继承XmlLanguageDriver,重装构造函数，使用自定义ParameterHandler
 * </p>
 * @author lincq
 * @date 2019/5/26 19:21
 */
public class MybatisXmlLanguageDriver extends XMLLanguageDriver {

    @Override
    public ParameterHandler createParameterHandler (MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new MybatisDefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }
}
