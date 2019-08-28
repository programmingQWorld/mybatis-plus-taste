package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.MybatisSessionFactoryBuilder;
import im.lincq.mybatisplus.taste.plugins.pagination.Pagination;
import im.lincq.mybatisplus.taste.spring.MybatisMapperRefresh;
import im.lincq.mybatisplus.taste.test.mapper.UserMapper;
import im.lincq.mybatisplus.taste.test.mysql.MySqlInjector;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.BasicConfigurator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lincq
 * @date 2019/8/28 09:08
 */
public class MybatisMapperRefreshTest {
    public static void main(String[] args) throws InterruptedException {

        BasicConfigurator.configure();

        InputStream in = UserMapper.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
        MybatisSessionFactoryBuilder mf = new MybatisSessionFactoryBuilder();
        mf.setSqlInjector(new MySqlInjector());

        Resource[] resource = new ClassPathResource[]{new ClassPathResource("mysql/UserMapper.xml")};
        SqlSessionFactory sessionFactory = mf.build(in);

        new MybatisMapperRefresh(resource,sessionFactory,0,5, true);
        boolean isReturn = false;
        SqlSession session=null;
        int i = 0;
        while (!isReturn){
            try {
                session = sessionFactory.openSession();
                session.clearCache();
                session.getConfiguration().setCacheEnabled(false);
                UserMapper userMapper = session.getMapper(UserMapper.class);
                System.out.println("select list row ... ");
                Thread.sleep(1000);
                // userMapper.updateById(new User(1L,  "linc" + i, 21,1));
                System.out.println(userMapper.selectListRow(new Pagination(1, 10)));
                resource[0].getFile().setLastModified(System.currentTimeMillis());
                session.commit();
                session.close();
                Thread.sleep(5000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (session != null) {
                    session.close();
                }
                Thread.sleep(5000);
            }
        }
        System.exit(0);
    }
}
