package im.lincq.mybatisplus.taste.test.proxy;

import im.lincq.mybatisplus.taste.test.proxy.mapper.IUserMapper;
import im.lincq.mybatisplus.taste.test.proxy.mapper.User;
import im.lincq.mybatisplus.taste.test.proxy.mapper.UserMapperImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <p>
 * mybatis 执行原理测试
 * </p>
 */
public class TestProxy {

    public static void main(String[] rags) {

        /**
         * 代理方式一
         */
        IUserMapper userMapper = MapperProxyFactory.getMapper(IUserMapper.class);
        User user = userMapper.selectById(1L);
        System.err.println((user == null) ? "代理失败" : user.getName());

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.err.println("\n");
        }

        /**
         * 代理方式二
         */
        MyMapperProxy<IUserMapper> userDaoProxy = new MyMapperProxy<IUserMapper>();
        IUserMapper mapper = userDaoProxy.bind(new UserMapperImpl());
        User user1 = mapper.selectById(1L);
        System.err.println(user1 == null ? "代理失败" : user1.getName() + user1.getAge());
    }
}

/**
 * 动态代理方式二
 *
 * @param <T>
 */
class MyMapperProxy<T extends IUserMapper> implements InvocationHandler {

    private T t;

    @SuppressWarnings("unchecked")
    public T bind(T t) {
        this.t = t;
        return (T) Proxy.newProxyInstance(t.getClass().getClassLoader(), t.getClass().getInterfaces(), this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object rst = null;
        before();
        rst = method.invoke(t, args);
        after();
        return rst;
    }

    private void before () {
        System.out.println("before ... ");
    }

    private void after () {
        System.out.println("after ... ");
    }

}