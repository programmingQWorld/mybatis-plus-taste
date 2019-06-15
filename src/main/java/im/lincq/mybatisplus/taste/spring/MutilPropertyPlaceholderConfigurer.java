package im.lincq.mybatisplus.taste.spring;


import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

public class MutilPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    /**
     * --------------------   使用配置       -------------------------
     * <bean id="placeholder" class="com.baomidou.mybatisplus.spring.MutilPropertyPlaceholderConfigurer">
     * 	  <property name="locations">
     * 		 <list>
     * 			<value>classpath:jdbc.properties</value>
     * 			<value>classpath*:*-placeholder.properties</value>
     * 		 </list>
     * 	  </property>
     * </bean>
     * -------------------------------------------------------
     * </p>
     *
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
                /**
                 * 其他使用自定义 mode 类型，使用 IP 例如 mode = 30
                 * 配置为  jdb.url_30_mode = xxxxx
                 */
            } else {

                /*  plus 认为 windows 是开发环境 */
                String OS = System.getProperty("os.name").toLowerCase();
                logger.info("os.name: " + OS);
                if (OS != null && OS.contains("windows")) {
                    mode = DEV;
                } else {
                    mode = ONLINE;
                }
            }
        }
        System.err.println("\n current system " + mode + " mode.");
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
