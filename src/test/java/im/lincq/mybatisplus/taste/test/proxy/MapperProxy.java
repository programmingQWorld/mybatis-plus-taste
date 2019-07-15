package im.lincq.mybatisplus.taste.test.proxy;


import im.lincq.mybatisplus.taste.test.proxy.mapper.IUserMapper;
import im.lincq.mybatisplus.taste.test.proxy.mapper.UserMapperImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * <p>类似 org.apache.ibatis.binding.MapperProxy </p>
 */
public class MapperProxy<T> implements InvocationHandler {

    private Class<T> methodInterface;

    public MapperProxy( Class<T> methodInterface ) {
        this.methodInterface = methodInterface;
    }

    /**
     * <p>
     *     Mybatis实现原理，在该方法中通过Method获取接口和方法名称，
     *     接口的全名相当于Mybatis XML中的namespace,方法名相当于具体一个方法的id。
     * </p>
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {

            return method.invoke(this, args);
        }

        /* 执行 SQL */
        return execute(method, args);
    }

    /**
     * <p>
     *     这里测试模拟已经注入了实现SQL
     *     通过动态代理 SqlSession 根据 namespace id 调用 org.apache.ibatis.binding.MapperMethod 对应方法执行
     * </p>
     */
    public Object execute( Method method, Object[] args ) {
        if ( IUserMapper.class.isAssignableFrom(methodInterface)) {
            System.out.println("类名：" + methodInterface.getName() +"，\n方法名：" + method.getName());
        }
        return new UserMapperImpl().selectById((Long)args[0]);
    }
}
