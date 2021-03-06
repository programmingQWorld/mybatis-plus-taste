package im.lincq.mybatisplus.taste;

import im.lincq.mybatisplus.taste.mapper.DBType;
import im.lincq.mybatisplus.taste.mapper.IMetaObjectHandler;
import im.lincq.mybatisplus.taste.mapper.ISqlInjector;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

public class MybatisSessionFactoryBuilder extends SqlSessionFactoryBuilder {

    private transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
        logger.info("exec: im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder.build(java.io.Reader, java.lang.String, java.util.Properties)");

        try {
            MybatisXMLConfigBuilder parser = new MybatisXMLConfigBuilder(reader, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                reader.close();
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

    @Override
    public SqlSessionFactory build (InputStream inputStream, String environment, Properties properties) {
        logger.info("exec im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder.build(java.io.InputStream, java.lang.String, java.util.Properties)");

        try  {
            MybatisXMLConfigBuilder  parser = new MybatisXMLConfigBuilder(inputStream, environment, properties);
            // configuration 的引用实例是MybatisConfiguration，这是mybatis-plus的扩展configuration.
            // configuration配置了
            Configuration configuration = parser.parse();
            return build(configuration);
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                inputStream.close();
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }

    //注入数据库类型
    public void setDbType( String dbType ) {
        MybatisConfiguration.DB_TYPE = DBType.getDBType(dbType);
    }

    // 注入表字段使用下划线命名
    public void  setDbColumnUnderline(boolean dbColumnUnderline) {
        MybatisConfiguration.DB_COLUMN_UNDERLINE = dbColumnUnderline;
    }

    // 注入 SQL注入器
    public void setSqlInjector(ISqlInjector sqlInjector) {
        MybatisConfiguration.SQL_INJECTOR = sqlInjector;
    }

    // 注入 元对象字段填充控制器
    public void setMetaObjectHandler(IMetaObjectHandler metaObjectHandler) {
        MybatisConfiguration.META_OBJECT_HANDLER = metaObjectHandler;
    }
}
