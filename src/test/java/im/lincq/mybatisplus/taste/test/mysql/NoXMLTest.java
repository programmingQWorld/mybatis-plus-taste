package im.lincq.mybatisplus.taste.test.mysql;

import im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder;
import im.lincq.mybatisplus.taste.test.mysql.entity.Test;
import im.lincq.mybatisplus.taste.test.mysql.entity.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.InputStream;
import java.util.List;

/**
 * @authors: lincq
 * @date: 2020/3/23 00:03
 **/
public class NoXMLTest {
    public static void main(String[] args) {
        InputStream in = NoXMLTest.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
        MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();
        SqlSessionFactory sessionFactory = mf.build(in);
        SqlSession sqlSession = sessionFactory.openSession();
        TestMapper testMapper = sqlSession.getMapper(TestMapper.class);
        List<Test> testList = testMapper.selectList(null);
        if (null != testList) {
            testList.forEach(System.out::println);
        }
    }
}
