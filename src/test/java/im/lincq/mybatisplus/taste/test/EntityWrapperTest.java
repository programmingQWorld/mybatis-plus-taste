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
        ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " ORDER BY id");
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
        ew.addFilter(" Order By id desc");
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals("Order By id desc", sqlSegment);
    }

    @Test
    public void test21 () {
        ew.addFilter("name={0}", "'123'").addFilterIfNeed(false, " ORDER BY id");
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals(" WHERE name='123'", sqlSegment);
    }

    @Test
    public void test22 () {
        ew.addFilter("name={0} order by id desc", "'123'");
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals(" WHERE name='123' order by id desc", sqlSegment);
    }

    @Test
    public void test23 () {
        ew.addFilter(" Order By id desc");
        String sqlSegment = ew.getSqlSegment();
        System.out.println(sqlSegment);
        Assert.assertEquals("Order By id desc", sqlSegment);
    }

}
