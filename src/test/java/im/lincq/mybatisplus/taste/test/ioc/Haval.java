package im.lincq.mybatisplus.taste.test.ioc;

/**
 * <p>
 *     哈佛汽车
 * </p>
 */
public class Haval implements ICar {

    public boolean start() {
        System.err.println(" 哈佛H9 点火启动... ");
        return true;
    }

    public void driver() {
        System.err.println(" 走你！哈佛H9 ... ");
    }

}
