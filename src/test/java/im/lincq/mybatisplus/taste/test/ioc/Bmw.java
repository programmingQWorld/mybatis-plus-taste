package im.lincq.mybatisplus.taste.test.ioc;

public class Bmw implements ICar {

    public boolean start() {
        System.err.println(" 宝马X6 点火启动... ");
        return false;
    }

    public void driver() {
        System.out.println(" 走你！宝马X6 ... ");
    }
}
