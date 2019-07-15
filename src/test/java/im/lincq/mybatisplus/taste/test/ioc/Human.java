package im.lincq.mybatisplus.taste.test.ioc;

/**
 * <p>
 *     试驾人员
 * </p>
 */
public class Human {

    private String name;

    public Human (String name) {
        this.name = name;
    }

    public void driver (ICar car) {
        if (car.start()) {
            car.driver();
            System.out.println(" 试驾结束 ");
        } else {
            System.out.println(" 熄火！ ");
        }
    }
}
