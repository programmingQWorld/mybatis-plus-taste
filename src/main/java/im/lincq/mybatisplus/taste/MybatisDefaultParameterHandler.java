package im.lincq.mybatisplus.taste;

import im.lincq.mybatisplus.taste.annotations.IdType;
import im.lincq.mybatisplus.taste.toolkit.IdWorker;
import im.lincq.mybatisplus.taste.toolkit.TableInfo;
import im.lincq.mybatisplus.taste.toolkit.TableInfoHelper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.util.*;

/**
 * <p>
 *     自定义ParameterHandler，重装构造函数，填充插入方法主键ID
 * </p>
 * @author lincq
 * @date 2019/5/26 19:25
 */
public class MybatisDefaultParameterHandler extends DefaultParameterHandler {

    public MybatisDefaultParameterHandler (
            MappedStatement mappedStatement,
            Object parameterObject,
            BoundSql boundSql) {
        super(mappedStatement, processBatch(mappedStatement, parameterObject), boundSql);
    }


    /**
     * <p>
     *     批量（填充主键ID）
     * </p>
     * @param ms
     * @param parameterObject  插入数据库对象
     * @return
     */
    protected static Object processBatch (MappedStatement ms, Object parameterObject) {
        Collection<Object> parameters = getParameters(parameterObject);
        // 是集合，遍历集合，为对象填充主键ID.
        if (parameters != null) {
            List<Object> objectList = new ArrayList<>();
            for (Object param : parameters) {
                objectList.add(populateKeys(ms, parameterObject));
            }
            return objectList;
        } else {
            // 不是集合，直接为其填充主键ID
            return populateKeys(ms, parameterObject);
        }
    }

    protected static Collection<Object> getParameters(Object parameter) {
        Collection<Object> parameters = null;
        if (parameter instanceof Collection) {
            parameters = (Collection)parameter;
        } else if (parameter instanceof  Map) {
            Map parameterMap = (Map)parameter;
            if (parameterMap.containsKey("collection")) {
                parameters = (Collection)parameterMap.get("collection");
            } else if (parameterMap.containsKey("list")) {
                parameters = (List)parameterMap.get("list");
            } else if (parameterMap.containsKey("array")) {
                parameters = Arrays.asList((Object[])parameterMap.get("array"));
            }
        }
        return parameters;
    }

    /**
     * <p>填充主键ID</p>
     * @param ms
     * @param parameterObject 插入数据库对象
     * @return
     */
    protected static Object populateKeys (MappedStatement ms, Object parameterObject) {
        if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(parameterObject.getClass());
            if (tableInfo != null && tableInfo.getIdType() != null && tableInfo.getIdType().getKey() >= 2) {
                MetaObject metaParam = ms.getConfiguration().newMetaObject(parameterObject);
                Object idValue = metaParam.getValue(tableInfo.getKeyProperty());
                if (idValue == null) {
                    if ( tableInfo.getIdType() == IdType.ID_WORKER ) {
                        metaParam.setValue(tableInfo.getKeyProperty(), IdWorker.getId());
                    } else if ( tableInfo.getIdType() == IdType.UUID ) {
                        metaParam.setValue(tableInfo.getKeyProperty(), get32UUID());
                    }
                }
                return metaParam.getOriginalObject();
            }
        }
        return parameterObject;
    }

    /**
     * <p>获取去掉"-" UUID</p>
     */
    protected static String get32UUID () {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
