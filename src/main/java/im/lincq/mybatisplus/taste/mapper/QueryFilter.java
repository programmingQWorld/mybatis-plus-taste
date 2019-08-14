package im.lincq.mybatisplus.taste.mapper;

import im.lincq.mybatisplus.taste.toolkit.StringUtils;

import java.text.MessageFormat;

/**
 * 查询条件
 * @author hubin, lincq
 * @date 2019/8/15 00:59
 */
public class QueryFilter {

    protected StringBuffer queryFilter = new StringBuffer();

    /**
     * <p>
     * 添加查询条件
     * </p>
     * <p>
     * 例如：ew.addFilter("name={0}", "'123'") <br>
     * 输出：name='123'
     * </p>
     *
     * @param keyWord
     *            SQL关键字
     * @param filter
     *            SQL 片段内容
     * @param params
     *            格式参数
     * @return String
     */
    protected QueryFilter addFilter(String keyWord, String filter, Object... params) {
        if (StringUtils.isEmpty(filter)) {
            return this;
        }
        if (StringUtils.isNotEmpty(keyWord)) {
            queryFilter.append(keyWord);
        }
        if (null != params && params.length >= 1) {
            queryFilter.append(MessageFormat.format(filter, params));
        } else {
            queryFilter.append(filter);
        }
        return this;
    }

    /**
     * <p>
     * 添加查询条件
     * </p>
     * <p>
     * 例如：ew.addFilter("name={0}", "'123'") <br>
     * 输出：name='123'
     * </p>
     *
     * @param filter
     *            SQL 片段内容
     * @param params
     *            格式参数
     * @return this
     */
    public QueryFilter addFilter(String filter, Object... params) {
        return addFilter(null, filter, params);
    }

    /**
     * <p>
     * 添加查询条件
     * </p>
     * <p>
     * 例如：ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " ORDER BY id") <br>
     * 输出：name='123'
     * </p>
     *
     * @param keyWord
     *            SQL关键字
     * @param willAppend
     *            判断条件 true 输出 SQL 片段，false 不输出
     * @param filter
     *            SQL 片段
     * @param params
     *            格式参数
     * @return this
     */
    protected QueryFilter addFilterIfNeed(boolean willAppend, String keyWord, String filter, Object... params) {
        if (willAppend) {
            addFilter(keyWord, filter, params);
        }
        return this;
    }

    /**
     * <p>
     * 添加查询条件
     * </p>
     * <p>
     * 例如：ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " ORDER BY id") <br>
     * 输出：name='123'
     * </p>
     *
     * @param willAppend
     *            判断条件 true 输出 SQL 片段，false 不输出
     * @param filter
     *            SQL 片段
     * @param params
     *            格式参数
     * @return this
     */
    public QueryFilter addFilterIfNeed(boolean willAppend, String filter, Object... params) {
        return addFilterIfNeed(willAppend, null, filter, params);
    }
}
