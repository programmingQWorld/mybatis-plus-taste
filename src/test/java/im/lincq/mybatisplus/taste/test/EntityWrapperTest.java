package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.mapper.EntityWrapper;
import im.lincq.mybatisplus.taste.test.mysql.entity.User;
import org.junit.Assert;
import org.junit.Test;

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
        ew.addFilter("name={0}", "'123'").orderBy("id", true);
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
        ew.addFilter("name={0}", "'123'").orderBy("id", true);
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals(" WHERE name='123'", sqlSegment);
    }

    @Test
    public void test22 () {
        ew.where("name={0}", "'123'").andIfNeed(false, "id=1").orderBy("id");
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
    public void testBlend() {
		/*
		 * 实体查询，混合 SQL 原样输出
		 */
        ew.setEntity(new User(1));
        ew.addFilter(null, "name={0}", "'123'").orderBy("id", false);
        String sqlSegment = ew.getSqlSegment();
        System.err.println("testBlend = " + sqlSegment);
        Assert.assertEquals(" AND name='123' ORDER BY id DESC ", sqlSegment);
    }

    @Test
    public void testNoTSQL() {
		/*
		 * 非 T-SQL 实体查询
		 */
        ew.setEntity(new User(1));
        ew.addFilter(null, "name={0}", "'123'").addFilterIfNeed(true, " order by id");
        String sqlSegment = ew.getSqlSegment();
        System.err.println("testNoTSQL = " + sqlSegment);
        Assert.assertEquals(" AND name='123' order by id", sqlSegment);
    }

    @Test
    public void testNoTSQL1() {
		/*
		 * 非 T-SQL 无实体查询
		 */
        ew.addFilter(null, "name={0}", "'123'").addFilterIfNeed(false, " order by id");
        String sqlSegment = ew.getSqlSegment();
        System.err.println("testNoTSQL1 = " + sqlSegment);
        Assert.assertEquals(" WHERE name='123'", sqlSegment);
    }

}
