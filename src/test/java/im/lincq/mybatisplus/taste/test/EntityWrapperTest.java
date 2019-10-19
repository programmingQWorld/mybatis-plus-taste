package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder;
import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import im.lincq.mybatisplus.taste.test.mapper.UserMapper;
import im.lincq.mybatisplus.taste.test.mysql.MyMetaObjectHandler;
import im.lincq.mybatisplus.taste.test.mysql.entity.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author lincq
 * @date 2019/8/13 21:36
 */
public class EntityWrapperTest {

    /*
    * User 查询包装器
    * */
    private EntityWrapper<User> ew = new EntityWrapper<User>();

    @Test
    public void test () {
        /* 无条件测试 */
        Assert.assertNull(ew);
    }

    @Test
    public void test1 () {
        ew.setEntity(new User(1));
        ew.addFilter("name={0}", "'123'");
        ew.orderBy("id", true);
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals(" AND name='123'", sqlSegment);
    }

    @Test
    public void test2 () {
        ew.setEntity(new User(1));
        ew.addFilter("name={0} order by id desc", "'123'");
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals(" AND name='123' order by id desc", sqlSegment);
    }

    @Test
    public void test3() {
        ew.setEntity(new User(1));
        ew.orderBy("id", true);
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals("Order By id desc", sqlSegment);
    }

    @Test
    public void test21 () {
        ew.addFilter("name={0}", "'123'");
        ew.orderBy("id", true);
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals(" WHERE name='123'", sqlSegment);
    }

    @Test
    public void test22 () {
        ew.where("name={0}", "'123'").addFilterIfNeed(false, "id=1").orderBy("id");
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals(" WHERE name='123' ORDER by id DESC", sqlSegment);
    }

    @Test
    public void test23 () {
        ew.orderBy(" id",false);
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals("Order By id DESC", sqlSegment);
    }

    @Test
    public void testNoTSQL() {
		/*
		 * 非 T-SQL 实体查询
		 */
        ew.setEntity(new User(1));
        ew.addFilter("name={0}", "'123'").addFilterIfNeed(true, " order by id");
        String sqlSegment = ew.getSqlSegment();
        System.err.println("testNoTSQL = " + sqlSegment);
        Assert.assertEquals(" AND name='123' order by id", sqlSegment);
    }

    @Test
    public void testNoTSQL1() {
		/*
		 * 非 T-SQL 无实体查询
		 */
        ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " order by id");
        String sqlSegment = ew.getSqlSegment();
        System.err.println("testNoTSQL1 = " + sqlSegment);
        Assert.assertEquals(" WHERE name='123'", sqlSegment);
    }

    @Test
    public void testSQL11 () {
        /*
        * 实体待查询使用方法 输出看结果
        * */
        ew.setEntity(new User(1));
        ew.where("name={0}", "zhangsan").and("id=1")
                .orNew("status={0}", "0").or("status=1")
                .notLike("nlike", "notvalue")
                .andNew("new=xx").like("hhh", "ddd")
                .andNew("pwd=11").isNotNull("n1,n2").isNull("n3")
                .groupBy("x1").groupBy("x2,x3")
                .having("x1=11").having("x3=433")
                .orderBy("dd").orderBy("d1,w2")
        ;

        System.out.println(ew.getSqlSegment());
    }

    @Test
    public void testNull () {
        ew.orderBy(null);
        String part = ew.getSqlSegment();
        Assert.assertNull(part);
    }

    @Test
    public void testNull2() {
        ew.like(null, null).where("aa={0}", "'bb'").orderBy(null);
        String sqlPart = ew.getSqlSegment();
        System.out.println(sqlPart);
        Assert.assertEquals("WHERE (aa='bb')", sqlPart);
    }

    /**
     * 测试带单引号的值是否不会再次添加单引号
     */
    @Test
    public void testNul14() {
        ew.where("id={0}", "'11'").and("name={0}", 22);
        String sqlPart = ew.getSqlSegment();
        System.out.println("sql ==> " + sqlPart);
        Assert.assertEquals("WHERE (id='11' AND name=22)", sqlPart);
    }

    /**
     * 测试带不带单引号的值是否会自动添加单引号
     */
    @Test
    public void testNul15() {
        ew.where("id={0}", "11").and("name={0}", 22);
        String sqlPart = ew.getSqlSegment();
        System.out.println("sql ==> " + sqlPart);
        Assert.assertEquals("WHERE (id='11' AND name=22)", sqlPart);
    }

    @Test
    public void testInsertFill () {
        BasicConfigurator.configure();

        InputStream in = getClass().getClassLoader().getResourceAsStream("mybatis-config.xml");
        MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();

        mf.setMetaObjectHandler(new MyMetaObjectHandler());
        SqlSessionFactory sqlSessionFactory = mf.build(in);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = new User();
        user.setAge(101);
        //user.setId(8796);
        System.out.println(userMapper.insert(user));
    }

    @Test
    public void testPlus () {
        System.out.println(1 * 0.5 == 0.5);
    }

    @Test
    public void testSet () {
        Set<String> set = new TreeSet<>((o1, o2) -> {
            return -o1.compareTo(o2);
        });
        set.add("A");
        set.add("G");
        set.add("E");
        set.add("C");

        for(String s:set) {
            System.out.println(s);
        }
    }

    @Test
    public void testString () {
        String s1 = "a" + "c";
        String s2 = new String(s1);
        if (s2 == "ac") {
            System.out.println("==");
        }
        if (s2.equals("ac")) {
            System.out.println("equals");
        }
    }



}
