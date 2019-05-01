package im.lincq.mybatisplus.taste.test;

import im.lincq.mybatisplus.taste.test.entity.User;
import im.lincq.mybatisplus.taste.toolkit.TableInfo;
import im.lincq.mybatisplus.taste.toolkit.TableInfoHelper;

public class OtherTest {
    public static void main(String[] args) {
        test();
    }
    public static void test () {
        TableInfo table = TableInfoHelper.getTableInfo(User.class);
        System.out.println(table);
    }
}
