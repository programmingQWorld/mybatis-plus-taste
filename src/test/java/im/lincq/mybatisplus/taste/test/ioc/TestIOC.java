package im.lincq.mybatisplus.taste.test.ioc;

/**
 * IOC演示
 */
public class TestIOC {

    /**
     * IOC 控制反转，依赖倒置
     */
    public static void main(String[] args) {

        // new 一个三毛
        Human sanMao = new Human("三毛");

        // 三毛试驾宝马X6
        sanMao.driver(new Bmw());

        // 三毛试驾哈佛H9
        sanMao.driver(new Haval());

    }

}
