package im.lincq.mybatisplus.taste.spring;


import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * <p>spring 根据不同配置运行模式，加载对应配置内容。</p>
 * <p>
 *     运行模式参数 key 配置 configEnv 默认 sysRunmode<br>
 *     online 线上 ， dev 开发 ， test 测试<br>
 *     首先环境变量中获取，变量名：sysRunmode 变量值：dev <br>
 *     如果不存在 JVM -D选项 参数中获取，例如：-DsysRunmode=dev <br>
 * </p>
 *
 * 例如：设置不同环境的数据库密码配置：
 * jdbc.password_dev_mode=1230600<br>
 * jdbc.password_test_mode=2001006<br>
 * jdbc.password_online_mode=#!Esd30210<br>
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
public class MutilPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private static final String ONLINE = "online";
    private static final String DEV = "dev";
    private static final String TEST = "test";

    /**
     * 运行环境配置变量名
     */
    private String configEnv = "sysRunmode";
    private Properties properties;

    /**
     * 获取当前运行模式
     * <p>
     *     首先在环境变量中获取，变量名：sysRunmode, 变量值: dev <br>
     *     如果不存在，将在JVM -D选项 参数中获取，例如: -DsysRunmode=dev <br>
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
                if (isLinux()) {
                    mode = ONLINE;
                } else {
                    mode = DEV;
                }
                System.err.println("-DSysRunmode=" + mode +"_mode");
                return mode;
            }
        }
        System.err.println("\n current system " + mode + " mode.");
        return mode;
    }

    /**
     * 判断是否为Linux环境
     * */
    protected boolean isLinux () {
        String OS = System.getProperty("os.name").toLowerCase();
        logger.info("os.name: " + OS);
        return !OS.contains("windows");
    }

    /**
     * <p>
     * spring 加载完配置文件，依靠父类  PropertiesLoaderSupport 方法 mergeProperties() 合并<br>
     * 重载该方法实现不同环境配置选择。
     * </p>
     */
    @Override
    protected Properties mergeProperties() throws IOException {
        this.properties = convertMergeProperties(super.mergeProperties());
        return this.properties;
    }

    /**
     * <p>转换 prop 加载内容</p>
     * spring 容器加载 Properties 文件
     */
    protected Properties convertMergeProperties(Properties mergeProperties) {
        Properties prop = new Properties();
        String runMode = "_" + getRunMode() + "_mode";
        Set<Map.Entry<Object, Object>> es = mergeProperties.entrySet();
        for ( Map.Entry<Object, Object> entry : es ) {
            String key = (String) entry.getKey();
            String realKey = key;
            int idx = key.lastIndexOf("_mode");
            if ( idx > 0 ) {
                if ( key.contains(runMode) ) {
                    realKey = key.substring(0, key.lastIndexOf(runMode));
                } else {
                    /** 排除其他运行模式 */
                    realKey = null;
                }
            }
            /**
             * 抽取合法属性<br>
             * 如果某个属性为空抛出运行时异常
             */
            if ( realKey != null && !prop.containsKey(realKey) ) {
                Object value = null;
                if ( idx > 0 ) {
                    value = mergeProperties.get(realKey + runMode);
                } else {
                    value = mergeProperties.get(realKey);
                }
                if ( value != null ) {
                    prop.put(realKey, value);
                } else {
                    throw new RuntimeException("impossible empty property for " + realKey);
                }
            }
        }
        return prop;
    }

    /**
     * 获取当前运行模式，默认 DEV 开发模式。
     * <p>
     * 首先环境变量中获取，变量名：sysRunmode 变量值：dev <br>
     * 如果不存在 JVM -D选项 参数中获取，例如：-DsysRunmode=dev <br>
     * </p>
     */
    public String getRunMode() {
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
            }
            /**
             * 其他使用自定义 mode 类型，使用 IP 例如 mode = 30
             * 配置为  jdb.url_30_mode = xxxxx
             */
        } else {
            /**
             * Windows 认为是开发环境
             */
            if (isLinux()) {
                mode = ONLINE;
            } else {
                mode = DEV;
            }
        }
        System.err.println("-DsysRunmode=" + mode + "_mode");
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
