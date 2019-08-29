package im.lincq.mybatisplus.taste;

import im.lincq.mybatisplus.taste.annotations.IdType;
import im.lincq.mybatisplus.taste.mapper.IMetaObjectHandler;
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
     *
     *     变量 parameterObject： 方法参数，可能代表集合，也可能代表单独的表实体对象
     *     变量 parameters：           如多parameterObject是集合类型，则将其转为Collection<Object>类型
     *     变量 parameter：             循环中的变量，代表集合元素
     * </p>
     * @param ms
     * @param parameterObject  插入数据库对象
     * @return
     */
    protected static Object processBatch (MappedStatement ms, Object parameterObject) {

        if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
            /*
            * 只处理插入操作
            * */
            Collection<Object> parameters = getParameters(parameterObject);
            // 是集合，遍历集合，为对象填充主键ID.
            if (parameters != null) {
                List<Object> objectList = new ArrayList<>();
                for (Object parameter : parameters) {
                    if (parameter instanceof Map) {
                        /* map 插入不处理 */
                        objectList.add(parameter);
                    } else {
                        objectList.add(populateKeys(ms, parameter));
                    }
                }
                return objectList;
            } else {
                // 不是集合，直接为其填充主键ID
                return populateKeys(ms, parameterObject);
            }
        }
        return parameterObject;

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
                MetaObject metaObject = ms.getConfiguration().newMetaObject(parameterObject);
                Object idValue = metaObject.getValue(tableInfo.getKeyProperty());
                if (idValue == null || "".equals(idValue) ) {
                    if ( tableInfo.getIdType() == IdType.ID_WORKER ) {
                        metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.getId());
                    } else if ( tableInfo.getIdType() == IdType.UUID ) {
                        metaObject.setValue(tableInfo.getKeyProperty(), get32UUID());
                    }
                }
                /* 自定义元对象填充控制器 */
                IMetaObjectHandler metaObjectHandler =  MybatisConfiguration.META_OBJECT_HANDLER;
                if (null != metaObjectHandler) {
                    metaObjectHandler.insertFill(metaObject);
                }
                return metaObject.getOriginalObject();
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
