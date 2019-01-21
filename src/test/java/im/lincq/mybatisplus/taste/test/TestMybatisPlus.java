package im.lincq.mybatisplus.taste.test;

import com.alibaba.druid.pool.DruidDataSource;
import im.lincq.mybatisplus.taste.handler.BeanHandler;
import im.lincq.mybatisplus.taste.mapper.SimpleMapper;
import im.lincq.mybatisplus.taste.toolkit.IdWorker;

public class TestMybatisPlus {

    private static DruidDataSource getDataSource() {
        TestDBConnection dbd = new TestDBConnection();
        dbd.initDruidDataSourceFactory();
        return dbd.getDruidDataSource();
    }

    public static void testInsert (SimpleMapper mapper) {
        /*入库对象*/
        TestUser user = new TestUser();
        user.setName("lin-cq");
        // 实岁
        user.setAge(21);
        System.out.println("准备入库user对象：" + user);
        mapper.insert(TestUser.class, user);
        System.out.println("入库后的user对象已有了id返回：" + user);
    }
    public static void main(String[] args) {

        System.out.println("生成唯一ID" + IdWorker.getId());


        SimpleMapper mapper = new SimpleMapper(getDataSource());
        //testInsert(mapper);

        /* 读取对象*/
        TestUser userInDb =  mapper.select(TestUser.class, 2L);
        System.out.println("数据库中查询出来的user对象 " + userInDb);

        /*换一种方式读取*/
        String mySql = "select * from test_user where id = ?";
        TestUser userInDb2 = mapper.query(mySql, new BeanHandler<TestUser>(TestUser.class), 2L);
        System.out.println("使用CQRS的query方式读取user对象 " + userInDb2);

        /* 更新这条记录 */
        userInDb2.setAge(22);
        mapper.update(TestUser.class, userInDb2);

        /*删除一条记录*/
        System.out.println("删除对象，得到命令影响行数：" + mapper.delete(TestUser.class, 2L));
    }
}
