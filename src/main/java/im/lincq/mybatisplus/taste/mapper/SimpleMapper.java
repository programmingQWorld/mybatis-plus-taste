package im.lincq.mybatisplus.taste.mapper;

import com.sun.tools.internal.xjc.reader.RawTypeSet;
import im.lincq.mybatisplus.taste.handler.BeanHandler;
import im.lincq.mybatisplus.taste.handler.PreparedStatementHandler;
import im.lincq.mybatisplus.taste.handler.ResultSetHandler;

import javax.sql.DataSource;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

public class SimpleMapper {
    private DataSource ds;
    private boolean sequence;
    private PreparedStatementHandler psh;

    public SimpleMapper(DataSource ds) {
        this.ds = ds;
        this.psh = PreparedStatementHandler.getInstance();
        sequence();
    }

    private void sequence() {
        Connection conn = this.getConnection();
        try {
            sequence = conn.getMetaData().getDatabaseProductName().toLowerCase().contains("oracle");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close(conn);
        }
    }

    public Connection getConnection () {
        try {
            return this.ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void close(Connection conn) {
        try {
            if (conn != null && conn.getAutoCommit() ) {
                conn.close();
            }
        } catch ( SQLException e ) {
            throw new RuntimeException(e);
        }
    }
    private void close(PreparedStatement stmt, Connection conn) {
        try {
            if (stmt != null ) {
                stmt.close();
            }
            close(conn);
        } catch ( SQLException e ) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 持久化对象
     * @param cls       对象类信息
     * @param bean   bean实例
     * @param <T>    泛型约束
     * @return             整数值：effected rows.
     */
    public <T> int insert( Class<T> cls, T bean) {
        return this.insert( this.getConnection(), cls, bean);
    }
    public <T> int insert(Connection conn, Class<T> cls, T bean) {
        return this.insert(conn, cls, bean, false);
    }

    /**
     * 持久化数据对象
     * @param conn
     * @param cls
     * @param bean
     * @param customKey 持久化的对象是否有id属性数据. false:没有 true:有
     * @param <T>
     * @return
     */
    public <T> int insert (Connection conn, Class<T > cls, T bean, boolean customKey) {
        int rows = 0;
        PreparedStatement stmt = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
            System.out.println("...lincq...logs..." + beanInfo);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            String table = camel2underscore(cls.getSimpleName());
            // questionMarks 为sql占位符 '?'
            String columns = "", questionMarks = "";

            Object[] params = customKey ? new Object[pds.length] : new Object[pds.length - 1];

            int j = 0;
            for (PropertyDescriptor pd : pds) {
                Method getter = pd.getReadMethod();
                String name = pd.getName();
                Object value = getter.invoke(bean);
                /*  非自定义主键，则ID作为主键且使用序列或自增主键*/
                if (!customKey && name.equals("id")) {
                    if ( sequence ) {
                        columns += "id";
                        questionMarks += table + "_SEQ.NEXTVAL";
                    }
                } else {
                    columns += camel2underscore(name) + ",";
                    questionMarks += "?,";
                    params[j] = value;
                    j++;
                }
            }
            // questionMarks 代表问号的字符串. -1 是去掉后面的逗号
            columns = columns.substring(0, columns.length() - 1);
            questionMarks = questionMarks.substring(0, questionMarks.length() - 1);
            String sql = String.format("insert into %s (%s) values (%s)", table, columns, questionMarks);

            // 日期格式参数调整
            sql = psh.adjust(sequence, sql, params);

            /*如果使用非自定义主键，则返回主键ID的值。。说的是持久化对象如果没有指定主键值，则返回该值回到对象中。*/
            // customKey 标记是否持久化对象是否有主键数据..
            if (!customKey) {
                if ( sequence ) {
                    // Oracle 数据库方式，不用过多考虑
                    String[] generatedColumns = {"id"};
                    stmt = conn.prepareStatement(sql, generatedColumns);
                } else {
                    // mysql或其它数据库
                    stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                }
            } else {
                stmt = conn.prepareStatement(sql);
            }
            this.fillStatement(stmt, params);
            try {
                rows = stmt.executeUpdate();
            } catch (SQLException e) {
                psh.print(sql, params);
                throw new RuntimeException(e);
            }

            if ( !customKey ) {
                ResultSet rs = stmt.getGeneratedKeys();
                long id = 0;
                if (rs.next()) {
                    id = rs.getLong(1);
                }
                for ( PropertyDescriptor pd : pds ) {
                    String name = pd.getName();
                    if ( name.equals("id") ) {
                        Method setter = pd.getWriteMethod();
                        setter.invoke(bean, id);
                        break;
                    }
                }
            }
        } catch (SQLException | IntrospectionException
                | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            close(stmt, conn);
        }
        return rows;
    }

    /**
     * 功能：通过stmt对象设置sql语句的参数.
     * @param stmt          PreparedStatement
     * @param params     sql语句占位符参数
     */
    private void fillStatement(PreparedStatement stmt, Object... params) {
        if (params == null) {
            return;
        }
        try {
            for (int i = 0; i < params.length; i++) {
                // hack oracle's bug (version <= 9)
                if (sequence && params[i] == null) {
                    stmt.setNull(i + 1, Types.VARCHAR);
                } else {
                    stmt.setObject(i + 1, params[i]);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询对象
     * @param cls       对象类信息
     * @param id        查询实例在数据库中的主键
     * @param <T>    泛型
     * @return              查询目标实例
     */
    public <T> T select(Class<T> cls, Long id) {
        return this.select(this.getConnection(), cls, id);
    }

    public <T> T select(Connection conn, Class<T> cls, long id) {
        String table = camel2underscore(cls.getSimpleName());
        return this.query(conn, "select * from " + table + " where id=?", new BeanHandler<T>(cls), id);
    }
    public <T> T query (String sql, ResultSetHandler<T>rsh, Object... params) {
        return this.query(this.getConnection(), sql, rsh, params);
    }
    /**
     * 功能: 根据实体类型，参数,在数据库对应表中获取一行记录，
     * 并转化为该类型的实例数据.
     * @param conn      参数
     * @param sql       sql语句
     * @param rsh       结果集处理器
     * @param params    sql语句参数.
     * @param <T>
     * @return
     */
    public <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        T result = null;
        try {
            sql = psh.adjust(sequence, sql, params);
            System.out.println(sql);
            stmt = conn.prepareStatement(sql);
            this.fillStatement(stmt, params);
            rs = stmt.executeQuery();
            result = rsh.handler(rs);
        } catch (SQLException e) {
            psh.print(sql, params);
            throw new RuntimeException(e);
        } finally {
            close (rs, stmt, conn);
        }
        return result;
    }


    public <T> int update ( Class<T> cls, T bean ) {
        return this.update(cls, bean, "id");
    }

    public <T> int update ( Connection conn, Class<T> cls, T bean ) {
        return this.update(conn, cls, bean, "id");
    }
    public <T> int update (Class<T> cls, T bean, String primaryKey) {
        return this.update(this.getConnection(), cls, bean, primaryKey);
    }
    public <T> int update (Connection conn, Class<T> cls, T bean, String primaryKey) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(cls, Object.class);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            Object[] params = new Object[pds.length];
            primaryKey = underscore2camel(primaryKey);
            Object id = 0;
            String columnAndQuestionMarks = "";
            int j = 0;

            // build sql String
            for (PropertyDescriptor pd : pds) {
                Method getter = pd.getReadMethod();
                String name = pd.getName();
                Object value = getter.invoke(bean);
                if (name.equals(primaryKey)) {
                    id = value;
                } else {
                    columnAndQuestionMarks += camel2underscore(name) + "=?,";
                    params[j] = value;
                    j++;
                }
            }
            params[j] = id;
            String table = camel2underscore(cls.getSimpleName());
            columnAndQuestionMarks = columnAndQuestionMarks.substring(0, columnAndQuestionMarks.length() - 1);
            String sql = String.format("update %s set %s where %s = ?", table, columnAndQuestionMarks, camel2underscore(primaryKey));
            return update(conn, sql, params);
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(Connection conn, String sql, Object... params) {
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            sql = psh.adjust(sequence, sql, params);
            stmt = conn.prepareStatement(sql);
            this.fillStatement(stmt, params);
             rows = stmt.executeUpdate();
        } catch (SQLException e) {
            psh.print(sql, params);
            throw new RuntimeException(e);
        } finally {
             close(stmt, conn);
        }
        return rows;
    }

    public <T> int delete(Class<T> cls, long id) {
        return this.delete(this.getConnection(), cls, id);
    }
    public <T> int delete(Connection conn, Class<T> cls, long id) {
        String sql = String.format("delete from %s where id = ?", camel2underscore(cls.getSimpleName()));
        return this.delete(conn, sql, id);
    }

    public <T> int delete(Connection conn, String sql, Object... params) {
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            sql = psh.adjust(sequence, sql, params);
            stmt = conn.prepareStatement(sql);
            this.fillStatement(stmt, params);
            rows =  stmt.executeUpdate();
        } catch (SQLException e) {
            psh.print(sql, params);
            throw new RuntimeException(e);
        } finally {
            close(stmt, conn);
        }
        return rows;
    }



    /**
     * 功能：释放sql对象相关链接资源
     * @param rs
     * @param stmt
     * @param conn
     */
    private void close (ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            close(stmt, conn);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
    private String camel2underscore (String camel) {
        return psh.camel2underscore(camel);
    }

    private String underscore2camel (String underscore) {
        return psh.underscore2camel(underscore);
    }
}
