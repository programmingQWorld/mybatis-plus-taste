package im.lincq.mybatisplus.taste;

import im.lincq.mybatisplus.taste.mapper.AutoMapper;
import im.lincq.mybatisplus.taste.mapper.AutoSqlInjector;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * replace default Configuration class
 */
public class MybatisConfiguration extends Configuration {

    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 初始化调用
     */
    public MybatisConfiguration() {
        System.err.println("mybatis-plus init success.");
    }

    /**
     * Mybatis加载Sql的顺序：<br></br>
     * 1. 加载xml中的sql <br>
     * 2. 加载SqlProvider中的sql <br>
     * 3. xml sql 与 sqlprovider 不能包含相同的sql id <br>
     * @param ms
     */
    @Override
    public void addMappedStatement(MappedStatement ms) {

        if (this.mappedStatements.containsKey(ms.getId())) {
            // 说明已经加载了xml中的节点,忽略mapper中的SqlProvider数据.
            logger.warn("mapper[{}] is ignored, because it's exists, maybe from xml file", ms.getId());
            return;
        }
        super.addMappedStatement(ms);
    }

    @Override
    public <T> T getMapper(Class<T > type, SqlSession sqlSession) {
        return super.getMapper(type, sqlSession);
    }

    /**
     * 重写 addMapper 方法
     * @param type      Mapper类型
     * @param <T>      泛型声明.
     */
    @Override
    public <T> void addMapper(Class<T> type) {
        super.addMapper(type);
        if (!AutoMapper.class.isAssignableFrom(type)) {
            return;
        }
        /* 自动注入SQL */
        new AutoSqlInjector(this).inject(type);
    }

    @Override
    public void addMappers(String packageName) {
        this.addMappers(packageName, Object.class);
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
        resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
        Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
        for (Class<?> mapperClass: mapperSet) {
            this.addMapper(mapperClass);
        }
    }


}
