package im.lincq.mybatisplus.taste.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class TestDBConnection {

    private static final Logger logger = Logger.getLogger("DBConnection");
    private static final String DB_CONFIG = "jdbc.properties";
    private static DruidDataSource dds = null;

    static void initDruidDataSourceFactory () {
        // 接收Properties或Map对象
        try {
            dds = (DruidDataSource) DruidDataSourceFactory.createDataSource(getInputStream(DB_CONFIG));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Properties getInputStream( String cfg ) {
        return getInputStream(TestDBConnection.class.getClassLoader().getResourceAsStream(cfg));
    }
    private static Properties getInputStream ( InputStream in ) {
        Properties p = null;
        try {
            p = new Properties();
            p.load(in);
        } catch (Exception e) {
            logger.severe("kisso read config file error");
        }
        return p;
    }
    protected static  DruidDataSource getDruidDataSource () {
        return dds;
    }

}
