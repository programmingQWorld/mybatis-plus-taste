package im.lincq.mybatisplus.taste;

import im.lincq.mybatisplus.taste.mapper.AutoSqlInjector;
import im.lincq.mybatisplus.taste.mapper.DBType;
import im.lincq.mybatisplus.taste.mapper.IMetaObjectHandler;
import im.lincq.mybatisplus.taste.mapper.ISqlInjector;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Logger;

/**
 * replace default Configuration class
 */
public class MybatisConfiguration extends Configuration {

    protected Logger logger = Logger.getLogger("MybatisConfiguration");


    /**
     * 数据库类型（默认 MySql）
     */
    public static DBType DB_TYPE = DBType.MYSQL;

    /**
     * 数据库字段使用下划线命名（默认 false）
     */
    public static boolean DB_COLUMN_UNDERLINE = false;

    /**
     * SQL 注入器，实现 ISqlInjector 或继承 AutoSqlInjector 自定义方法
     */
    public static ISqlInjector SQL_INJECTOR  = new AutoSqlInjector();;

    /**
     * Mapper 注册
     */
    public final MybatisPlusMapperRegistry mybatisPlusMapperRegistry = new MybatisPlusMapperRegistry(this);


    /**
     * 元对象字段填充控制器
     */
    public static IMetaObjectHandler META_OBJECT_HANDLER = null;

    /*
	 * 是否刷新mapper
	 */
    public static boolean IS_REFRESH = false;

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
     *
     * 调整后的sql优先级: xmlSql ->sqlProvider ->crudSql <br>
     * @param ms
     */
    @Override
    public void addMappedStatement(MappedStatement ms) {
        logger.fine("addMappedStatement: " );
        if (IS_REFRESH) {
            /*
			 * 支持是否自动刷新 XML 变更内容，开发环境使用【 注：生产环境勿用！】
			 */
            this.mappedStatements.remove(ms.getId());
        } else {
            if (this.mappedStatements.containsKey(ms.getId())) {
                // 说明已经加载了xml中的节点,忽略mapper中的SqlProvider数据.
                logger.severe("mapper["+ ms.getId() +"] is ignored, because it's exists, maybe from xml file");
                return;
            }
        }
        super.addMappedStatement(ms);
    }

    @Override
    public void setDefaultScriptingLanguage(Class<?> driver) {
        if (driver == null) {
            /* 设置自定义driver */
            driver = MybatisXmlLanguageDriver.class;
        }
        super.setDefaultScriptingLanguage(driver);
    }



    /**
     * Mapper注册
     */
    @Override
    public MapperRegistry getMapperRegistry() {
        return mybatisPlusMapperRegistry;
    }

    @Override
    public <T> void addMapper(Class<T> type) {
        mybatisPlusMapperRegistry.addMapper(type);
    }

    @Override
    public void addMappers(String packageName, Class<?> superType) {
        mybatisPlusMapperRegistry.addMappers(packageName, superType);
    }

    @Override
    public void addMappers(String packageName) {
        mybatisPlusMapperRegistry.addMappers(packageName);
    }

    @Override
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mybatisPlusMapperRegistry.getMapper(type, sqlSession);
    }

    @Override
    public boolean hasMapper(Class<?> type) {
        return mybatisPlusMapperRegistry.hasMapper(type);
    }

}
