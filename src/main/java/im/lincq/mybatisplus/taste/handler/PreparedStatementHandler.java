package im.lincq.mybatisplus.taste.handler;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用PreparedStatement类的辅助处理.
 * @author cover by lin-cq
 */
public final class PreparedStatementHandler {
    private static final PreparedStatementHandler psh = new PreparedStatementHandler();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static PreparedStatementHandler getInstance() {
        return psh;
    }

    public void pager(boolean sequence, StringBuffer sql, List<Object> params, int pagesize, int pageNo) {

    }

    /**
     * 功能： 驼峰单词转下划线隔开形式
     * <br>
     * 处理：
     * 使用正则表达式圆括号分组功能.匹配相邻两个字母（前为小写字母，后为大写字母）
     * 匹配到后，引用分组内容（1和2），将驼峰词中所有的匹配选项替换为 \1_\2
     * 如 userTicket --> rT  -->  user_Ticket --> user_ticket
     * 正则匹配及替换工作可以在String # replaceAll 方法中完成.
     *
     * @param camel 驼峰单词
     * @return      转化结果
     */
    public String camel2underscore (String camel) {
        camel = camel.replaceAll("([a-z])([A-Z])", "$1_$2");
        return camel.toLowerCase();
    }

    /**
     * 功能： 下划线单词转驼峰单词  如. user_ticket => userTicket
     * <br>
     * 处理：
     * 参数转为小写形式.使用正则匹配 _([a-z])，
     * 将匹配到的每一个位置替换为所引用分组内容的大写形式.
     *
     * @param underscore        下划线单词
     * @return                  转化结果--> 驼峰单词
     */
    public String underscore2camel(String underscore) {
        if ( !underscore.contains("_") ) {
            return underscore;
        }
        StringBuffer buf = new StringBuffer();
        underscore = underscore.toLowerCase();
        final Matcher m = Pattern.compile("_([a-z])").matcher(underscore);
        while (m.find()) {
            m.appendReplacement(buf, m.group(1).toUpperCase());
        }

        return m.appendTail(buf).toString();
    }

    /**
     *
     * 功能：调整Sql语句以及对应的参数值.
     * eg: select * from user where id = ? and crt_time between ? and ?
     * 上面示例sql语句中有三个占位符. 当占位符类型代表日期类型时候，处理传给sql语句的参数值为 yyyy-mm-dd hh24:mi:ss 形式的字符串
     * 有时候也会因为其它需要去改变占位符表现形式，
     * 如between ? and ? -->
     *   between to_Date(?, 'yyyy-mm-dd hh24:mi:ss') and to_Date(?, 'yyyy-mm-dd hh24:mi:ss')
     *
     * 处理：
     * 创建新的数组，初始化值为占位符('?')，代表sql语句中的占位符.
     * 若有特殊情况(eg:Date类型参数)则指定的占位符置换为合适的形式.
     *
     * 循环参数数组，判断参数类型，
     * 如果是Date类型，替换参数值形式为：yyyy-MM-dd HH:mm:ss 形式的字符串
     * 同时如果使用oracle数据库，则sql语句中对应参数的占位符'?'应当是 "to_Date(?, 'yyyy-mm-dd hh24:mi:ss')"
     *
     * @param sequence      数据库产品是否Oracle.
     * @param sql           Sql语句
     * @param params        构造Sql语句的参数
     * @return              调整后的Sql语句
     */
    public String adjust(boolean sequence, String sql, Object[] params) {

        int cols = params.length;
        Object[] args = new Object[cols];
        boolean oracleDateParamFound = false;
        for (int i = 0; i < cols; i++) {
            args[i] = "?";
            Object value = params[i];
            if (value == null) {
                continue;
            }
            if (value instanceof Date) {
                if (sequence) {
                    // oracle中日期值的一种格式
                    args[i] = "to_Date(?, 'yyyy-mm-dd hh24:mi:ss')";
                    oracleDateParamFound = true;
                }
                params[i] = sdf.format(value);
            } else if (value.getClass().isEnum()) {
                params[i] = value.toString();
            }
            if (oracleDateParamFound) {
                String format = sql.replaceAll("\\?", "%s");
                sql = String.format(format, args);
            }
        }
        return sql;
    }

    /**
     * 功能：打印执行sql语句及参数到控制台中，这会在sql执行出错场景中使用到，便于程序人员排错.
     * @param sql       sql 语句
     * @param params    sql 语句用到的参数
     */
    public void print(String sql, Object[] params) {
        if ( !match(sql, params) ) {
            System.out.println(sql);
            return;
        }

        int cols = params.length;
        Object[] values = new Object[cols];
        System.arraycopy(params, 0, values, 0, cols);

        for ( int i = 0; i < cols; i++ ) {
            Object value = values[i];
            // 日期类型和字符串类型的值要加上引号
            // 布尔类型的值转化为数字[01] 0->false, 1->true， 数值类型不添加引号.
            if (value instanceof Date) {
                values[i] = toQuote(sdf.format(value));
            } else if ( value instanceof String ) {
                values[i] = toQuote(value);
            } else if (value instanceof Boolean) {
                values[i] = (Boolean) value ? 1 : 0;
            }
        }
        // 替换sql语句中所有字符问号为具体参数值.
        // select * from tb_name where id = ? and crt_time = ?
        // select * from tb_name where id = %s and crt_time = %s
        // String.format(str_sql, values)
        String statement = String.format(sql.replaceAll("\\?", "%s"), values);
        System.out.println(statement);
    }


    /**
     * 功能：判断sql语句中问号（?）个数是否与实际参数个数相等
     * @param sql       sql语句
     * @param params    sql语句参数
     * @return          sql语句中问号（?）<b>个数</b>是否与实际参数<b>个数</b>相等
     */
    private boolean match (String sql, Object[] params) {
        Matcher m = Pattern.compile("(\\?)").matcher(sql);
        int count = 0;
        while (m.find()) {
            count++;
        }
        return count == params.length;
    }

    private String toQuote (Object value) {
        return "'" + value + "'";
    }

    public static void main(String[] args) {
        PreparedStatementHandler psh = PreparedStatementHandler.getInstance();
        System.out.println(psh.camel2underscore("userTicket"));


        int count = 0;
        Matcher m = Pattern.compile("\\?").matcher("abc_?ud_??");
        while (m.find()) {
            count++;

        }
        System.out.println(count);

    }


}



