package im.lincq.mybatisplus.taste.spring;


import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

public class MutilPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    /**
     * <p>
     * 运行环境<br>
     * online 线上 ， dev 开发 ， test 测试
     * </p>
     */
    private static final String ONLINE = "online";
    private static final String DEV = "dev";
    private static final String TEST = "test";

    /**
     * 运行环境配置变量名
     */
    private String configEnv = "spring.runmode";
    private Properties properties;

    /**
     * 获取当前运行模式
     * <p>
     *     首先在环境变量中获取，变量名：spring.runmode, 变量值: dev <br>
     *     如果不存在，将在JVM -D选项 参数中获取，例如: -Dspring.runmode=dev <br>
     * </p>
     * @return
     */
    public String getRunmode () {
        String mode = System.getenv(getConfigEnv());
        if ( mode == null || "".equals(mode) ) {
            mode = System.getProperty(getConfigEnv());
        }
        if ( mode != null ) {
            if ( ONLINE.equals(mode) ) {
                mode = ONLINE;
            } else if ( DEV.equals(mode) ) {
                mode = DEV;
            } else if ( TEST.equals(mode) ) {
                mode = TEST;
            } else {
                mode = DEV;
            }
        }
        System.err.println("\n system " + mode + " mode.");
        return mode;
    }

    public String getConfigEnv() {
        return configEnv;
    }

    public void setConfigEnv(String configEnv) {
        this.configEnv = configEnv;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
