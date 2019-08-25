package im.lincq.mybatisplus.taste;

import im.lincq.mybatisplus.taste.toolkit.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lincq
 * @date 2019/8/15 13:03
 */
public abstract class MybatisAbstractSQL<T> {

    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String AND_NEW = ") \n AND (";
    private static final String OR_NEW = ") \n OR (";


    public SQLCondition sql = new SQLCondition();

    /**
     * 子类泛型实现
     * @return 泛型实例
     */
    public abstract  T getSelf();

    public T WHERE (String conditions) {
        sql().where.add(conditions);
        sql().lastList = sql().where;
        return getSelf();
    }

    public T OR () {
        sql().lastList.add(OR);
        return getSelf();
    }

    public T OR_NEW () {
        sql().lastList.add(OR_NEW);
        return getSelf();
    }

    public T AND() {
        sql().lastList.add(AND);
        return getSelf();
    }

    public T AND_NEW() {
        sql().lastList.add(AND_NEW);
        return getSelf();
    }

    public T GROUP_BY(String columns) {
        sql().groupBy.add(columns);
        return getSelf();
    }

    public T HAVING(String conditions) {
        sql().having.add(conditions);
        sql().lastList = sql().having;
        return getSelf();
    }

    public T ORDER_BY(String columns) {
        sql().orderBy.add(columns);
        return getSelf();
    }

    private SQLCondition sql() {
        return sql;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sql().sql(sb);
        return sb.toString();
    }

    /**
     * SQL连接器
     */
    private static class SafeAppendable {
        private final Appendable a;
        private boolean empty = true;

        public SafeAppendable (Appendable a) {
            super();
            this.a = a;
        }

        public SafeAppendable append(CharSequence s) {
            try {
                if (empty && s.length() > 0) {
                    empty = false;
                }
                a.append(s);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public boolean isEmpty () {
            return empty;
        }
    }

    /**
     * SQL条件类
     * */
    private static class SQLCondition {
        List<String> where = new ArrayList<String>();
        List<String> having = new ArrayList<String>();
        List<String> groupBy = new ArrayList<String>();
        List<String> orderBy = new ArrayList<String>();
        List<String> lastList = new ArrayList<String>();
        List<String> andOr = new ArrayList<String>();

        public SQLCondition() {
            andOr.add(AND);
            andOr.add(OR);
            andOr.add(AND_NEW);
            andOr.add(OR_NEW);
        }


        /**
         * 构建SQL的条件
         * @param builder            连接器
         * @param keyWord        TSQL中的关键字
         * @param parts               SQL条件语句集合
         * @param open               其实符号
         * @param close               结束符号
         * @param conjunction   链接条件
         */
        // 通过or(New) and(New)方法添加的sql_condition存放在where集合中，实现为 step1: append语义关键词， step2: append sql_part
        private void sqlCause (SafeAppendable builder, String keyWord, List<String> parts, String open, String close, String conjunction) {

            parts = clearNull(parts);
            // parts集合不为空的情况下
            if (!parts.isEmpty()) {
                // 如果builder 不为空，则准备换行
                if (!builder.isEmpty()) {
                    builder.append("\n");
                }
                builder.append(keyWord);
                builder.append(" ");
                builder.append(open);

                String last = "__";

                for (int i = 0, n = parts.size(); i < n; i++) {
                    String part = parts.get(i);
                    if (i > 0) {
                        // 若当前 part 是 关键字，则拼接关键字. 继续下一层循环
                        // 若不是，如 part = "stauts=1"，同样要拼接关键字(调用sqlCause方法时指定的关键字conjunction)，再拼接part.
                        if (andOr.contains(part) || andOr.contains(last)) {
                            builder.append(part);
                            last = part;
                            continue;
                        } else {
                            builder.append(conjunction);
                        }
                    }
                    builder.append(part);
                }
                builder.append(close);
            }
        }

        /**
         * 清除LIST中的NULL和空字符串
         * @param parts 原LIST列表
         * @return List
         */
        private List<String> clearNull(List<String> parts) {
            List<String> temps = new ArrayList<String>();
            for (String part : parts) {
                if (StringUtils.isEmpty(part)) {
                    continue;
                }
                temps.add(part);
            }
            return temps;
        }

        /**
         * 按标准顺序链接并构建SQL
         *
         * @param builder 连接器
         * @return
         */
        private String buildSQL (SafeAppendable builder) {
            sqlCause(builder, "WHERE", where, "(", ")", AND);
            sqlCause(builder, "GROUP BY", groupBy, "(", ")", ",");
            sqlCause(builder, "HAVING", having, "(", ")", OR);
            sqlCause(builder, "ORDER BY", orderBy, "(", ")", ",");
            return builder.toString();
        }

        public String sql (Appendable a) {
            SafeAppendable builder = new SafeAppendable(a);
            return buildSQL(builder);
        }

    }
}
