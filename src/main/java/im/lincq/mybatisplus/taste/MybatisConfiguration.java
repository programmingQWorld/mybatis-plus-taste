package im.lincq.mybatisplus.taste;

import im.lincq.mybatisplus.taste.mapper.DBType;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

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
        if (this.mappedStatements.containsKey(ms.getId())) {
            // 说明已经加载了xml中的节点,忽略mapper中的SqlProvider数据.
            logger.severe("mapper["+ ms.getId() +"] is ignored, because it's exists, maybe from xml file");
            return;
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

}
