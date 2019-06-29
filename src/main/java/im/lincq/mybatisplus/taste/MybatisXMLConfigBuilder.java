package im.lincq.mybatisplus.taste;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

public class MybatisXMLConfigBuilder extends BaseBuilder {

    private transient Logger logger = LoggerFactory.getLogger(getClass());

    private boolean parsed;
    private XPathParser parser;
    private String environment;
    private ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

    public MybatisXMLConfigBuilder(Configuration configuration) {
        super(configuration);
    }

    public MybatisXMLConfigBuilder(Reader reader) {
        this(reader, null, null);
    }

    public MybatisXMLConfigBuilder(Reader reader, String environment) {
        this(reader, environment, null);
    }

    public MybatisXMLConfigBuilder(Reader reader, String environment, Properties props) {
        this(new XPathParser(reader, true, props, new XMLMapperEntityResolver()), environment, props);
    }
    public MybatisXMLConfigBuilder(InputStream inputStream){
        this(inputStream, null, null);
    }
    public MybatisXMLConfigBuilder(InputStream inputStream, String environment){
        this(inputStream, environment, null);
    }

    public MybatisXMLConfigBuilder(InputStream inputStream, String environment, Properties properties) {
        this(new XPathParser(inputStream, true, properties, new XMLMapperEntityResolver()), environment, properties);
    }

    public MybatisXMLConfigBuilder(XPathParser xPathParser, String environment, Properties properties) {
        // replace default Configuration class;
        super(new MybatisConfiguration());
        ErrorContext.instance().resource("SQL Mapper Configuration");
        this.configuration.setVariables(properties);
        this.parsed = false;
        this.environment = environment;
        this.parser = xPathParser;
    }


    // 这是在转换什么呢？
    // 具体转换逻辑在本类parseConfiguration方法中，
    // 该方法对本类configuration对象的属性数据初始化.
    public Configuration parse () {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        if (this.parsed) {
            throw new BuilderException("Each XMLConfigBuilder can only be used once.");
        }
        parsed = true;
        parseConfiguration(parser.evalNode("/configuration"));
        return configuration;
    }

    private void parseConfiguration(XNode root) {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        try {
            Properties settings = settingsAsPropertiess(root.evalNode("settings"));
            //issue #117 read properties first
            propertiesElement(root.evalNode("properties"));
            loadCustomVfs(settings);
            typeAliasesElement(root.evalNode("typeAliases"));
            pluginElement(root.evalNode("plugins"));
            objectFactoryElement(root.evalNode("objectFactory"));
            objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
            reflectionFactoryElement(root.evalNode("reflectionFactory"));
            settingsElement(settings);
            //settingsElement(root.evalNode("settings"));
            // read it after objectFactory and objectWrapperFactory issue #631
            environmentsElement(root.evalNode("environments"));
            databaseIdProviderElement(root.evalNode("databaseIdProvider"));
            typeHandlerElement(root.evalNode("typeHandlers"));
            // 这里是注册Mapper信息的.
            mapperElement(root.evalNode("mappers"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }

    private Properties settingsAsPropertiess (XNode context) {
        if (context == null) {
            return new Properties();
        }
        Properties props = context.getChildrenAsProperties();
        // Check that all settings are known to the configuration class
        MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
        for (Object key : props.keySet()) {
            if (!metaConfig.hasSetter(String.valueOf(key))) {
                throw new BuilderException(
                        "The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
            }
        }
        return props;
    }

    private void loadCustomVfs(Properties props) throws ClassNotFoundException {
        String value = props.getProperty("vfsImpl");
        if (value != null) {
            String[] clazzes = value.split(",");
            for (String clazz : clazzes) {
                if (!clazz.isEmpty()) {
                    configuration.setVfsImpl(Resources.classForName(clazz));
                }
            }
        }
    }
    /**
     *
     * <properties resource="jdbc.property" url = "123.property">
     *         <property name="configruationXXX" value="13dsc23" />
     *  </properties>
     * @param context
     */
    private void propertiesElement (XNode context) throws Exception {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        // 获取properties标签下声明的属性,作为基本的 map 容器 -- defaults
        // 获取properties标签的属性值: resource url ,加载其中指定的属性文件数据到defaults中
        if (context != null) {
            Properties defaults = context.getChildrenAsProperties();
            String resource = context.getStringAttribute("resource");
            String url = context.getStringAttribute("url");
            if (resource != null && url != null) {
                throw new BuilderException("The properties element cannot specify both a URL and a resource based a property file reference. Please specify one or other");
            }
            if  (resource != null)  {
                defaults.putAll(Resources.getResourceAsProperties(resource));
            } else if (url != null) {
                defaults.putAll(Resources.getUrlAsProperties(url));
            }
            Properties vars = configuration.getVariables();
            logger.info("configuration.getVariables: " + vars);
            if (vars != null) {
                defaults.putAll(vars);
            }
            parser.setVariables(vars);
            configuration.setVariables(defaults);
        }

    }

    /**
     * Configuratioin tag nested typeAliases
     * <typeAliases>
     *         <typeAlias type="im.lincq.mybatisplus.taste.test.User" alias="User" />
     * </typeAliases>
     * @param parent
     */
    private void typeAliasesElement (XNode parent) {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                if ("package".equals(child.getName())) {
                    // 1. name属性是包路径名称
                    // 2. 注册别名
                    String typeAliasPackage = child.getStringAttribute("name");
                    configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage);
                } else {
                    String type = child.getStringAttribute("type");
                    String alias = child.getStringAttribute("alias");

                    try {
                        Class<?> clazz = Resources.classForName(type);
                        if (alias == null) {
                            typeAliasRegistry.registerAlias(clazz);
                        } else {
                            typeAliasRegistry.registerAlias(alias, clazz);
                        }
                    } catch (Exception e) {
                        throw new BuilderException ("Error registering typeAlias for " + alias + " . Cause:" + e, e);
                    }
                }
            }
        }
    }

    /**
     *     <plugins>
     *         <plugin interceptor="im.lincq.mybatisplus.taste.mapper.AutoSqlInjector">
     *             <property name="d" value="dd" />
     *             <property name="rc" value="sdc" />
     *         </plugin>
     *         <plugin interceptor="im.lincq.mybatisplus.taste.mapper.XXXPageInjector"></plugin>
     *     </plugins>
     *     拿到plugin tag 's interceptor 属性值
     *     获取plugin标签下的proeprty tag as property map.
     *     new Instance of Custom interceptor
     *     set properties for interceptor instance.
     *     registry interceptor.
     * @param parent
     */
    private void pluginElement (XNode parent) throws Exception {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#pluginElement");
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                String interceptor = child.getStringAttribute("interceptor");
                Properties properties = child.getChildrenAsProperties();
                Interceptor interceptorInstance = (Interceptor)resolveClass(interceptor).newInstance();
                interceptorInstance.setProperties(properties);
                //configuration.getInterceptors().add(interceptorInstance); # 会引起 java.lang.UnsupportedOperationException
                configuration.addInterceptor(interceptorInstance);

            }
        }
    }

    /**
     *     <objectFactory type="im.lincq.mybatisplus.taste.xx.XXXObjectFactory">
     *         <property name="123" value="321" />
     *         <property name="123" value="321" />
     *         <property name="123" value="321" />
     *     </objectFactory>
     * @param context
     */
    private void objectFactoryElement (XNode context) throws Exception {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties properties = context.getChildrenAsProperties();
            ObjectFactory objectFactory = (ObjectFactory) resolveClass(type).newInstance();
            objectFactory.setProperties(properties);
            configuration.setObjectFactory(objectFactory);
        }
    }

    /**
     *     <objectWrapperFactory type="im.lincq.mybatisplus.taste.wrapperfactory.XXXWrapperFactory">
     *         <property name="231" value="23" />
     *     </objectWrapperFactory>
     * @param context
     */
    private void objectWrapperFactoryElement (XNode context) throws Exception {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        if (context != null) {
            String type = context.getStringAttribute("type");
            ObjectWrapperFactory objectWrapperFactory = (ObjectWrapperFactory)resolveClass(type).newInstance();
            // 这里还可以加载属性标签数据的,但是这里没有.
            configuration.setObjectWrapperFactory(objectWrapperFactory);
        }
    }

    /**
     * dtd 文件中好像没有这个标签定义reflectionFactory
     * @param context reflectionFactory
     */
    private void reflectionFactoryElement (XNode context) throws IllegalAccessException, InstantiationException {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        if (context != null) {
            String type = context.getStringAttribute("type");
            ReflectorFactory factory = (ReflectorFactory) resolveClass(type).newInstance();
            configuration.setReflectorFactory(factory);
        }
    }


    /**
     * <settings>
     *   <setting name="cacheEnabled" value="true"/>
     *   <setting name="lazyLoadingEnabled" value="true"/>
     *   <setting name="multipleResultSetsEnabled" value="true"/>
     *   <setting name="useColumnLabel" value="true"/>
     *   <setting name="useGeneratedKeys" value="false"/>
     *   <setting name="autoMappingBehavior" value="PARTIAL"/>
     *   <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
     *   <setting name="defaultExecutorType" value="SIMPLE"/>
     *   <setting name="defaultStatementTimeout" value="25"/>
     *   <setting name="defaultFetchSize" value="100"/>
     *   <setting name="safeRowBoundsEnabled" value="false"/>
     *   <setting name="mapUnderscoreToCamelCase" value="false"/>
     *   <setting name="localCacheScope" value="SESSION"/>
     *   <setting name="jdbcTypeForNull" value="OTHER"/>
     *   <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
     * </settings>
     * @param props
     */
    private void settingsElement (Properties props) {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        configuration.setAutoMappingBehavior(AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
        configuration.setAutoMappingUnknownColumnBehavior(AutoMappingUnknownColumnBehavior
                .valueOf(props.getProperty("autoMappingUnknownColumnBehavior", "NONE")));
        configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
        configuration.setProxyFactory((ProxyFactory)createInstance(props.getProperty("proxyFactory")));
        configuration.setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
        configuration.setAggressiveLazyLoading(booleanValueOf(props.getProperty("aggressiveLazyLoading"), true));
        configuration.setMultipleResultSetsEnabled(booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
        configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
        configuration.setUseGeneratedKeys(booleanValueOf(props.getProperty("useGeneratedKeys"), false));
        configuration.setDefaultExecutorType(ExecutorType.valueOf(props.getProperty("defaultExecutorType", "SIMPLE")));
        configuration.setDefaultStatementTimeout(integerValueOf(props.getProperty("defaultStatementTimeout"), null));
        configuration.setDefaultFetchSize(integerValueOf(props.getProperty("defaultFetchSize"), null));
        configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
        configuration.setSafeRowBoundsEnabled(booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
        configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
        configuration.setJdbcTypeForNull(JdbcType.valueOf(props.getProperty("jdbcTypeForNull", "OTHER")));
        configuration.setLazyLoadTriggerMethods(stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
        configuration.setSafeResultHandlerEnabled(booleanValueOf(props.getProperty("safeResultHandlerEnabled"), true));
        configuration.setDefaultScriptingLanguage(resolveClass(props.getProperty("defaultScriptingLanguage")));
        configuration.setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
        configuration.setLogPrefix(props.getProperty("logPrefix"));
        configuration.setLogImpl(resolveClass(props.getProperty("logImpl")));
        configuration.setConfigurationFactory(resolveClass(props.getProperty("configurationFactory")));
    }

    /**
     *     <environments default="development">
     *         <environment id="development">
     *             <transactionManager type="JDBC" />
     *             <dataSource type="POOLED">
     *                 <property name="driver" value="com.mysql.jdbc.Driver" />
     *                 <property name="url" value="jdbc:mysql://localhost:3306/mplus" />
     *                 <property name="username" value="root" />
     *                 <property name="password" value="" />
     *             </dataSource>
     *         </environment>
     *     </environments>
     * @param context
     */
    private void environmentsElement (XNode context) throws Exception {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        // 如果当前 env 未设置,则设置环境. 并且找到对应 env 初始化env信息
        if (context != null) {
            if (environment == null) {
                environment = context.getStringAttribute("default");
            }
            // 在多个环境场景中匹配当前使用的 env
            for (XNode child : context.getChildren()) {
                String id = child.getStringAttribute("id");
                if (isSpecifiedEnvironment(id)) {
                    TransactionFactory txFactory =  transactionManagerElement(child.evalNode("transactionManager"));
                    DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
                    DataSource dataSource = dsFactory.getDataSource();
                    Environment.Builder environmentBuilder = new Environment.Builder(id).transactionFactory(txFactory).dataSource(dataSource);
                    configuration.setEnvironment(environmentBuilder.build());
                }
            }
        }
    }

    /**
     * <databaseIdProvider type="DB_VENDOR">
     *   <property name="SQL Server" value="sqlserver"/>
     *   <property name="DB2" value="db2"/>
     *   <property name="Oracle" value="oracle" />
     * </databaseIdProvider>
     * @param context
     */
    private void databaseIdProviderElement (XNode context) throws Exception {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        DatabaseIdProvider databaseIdProvider = null;
        if (context != null) {
            String type = context.getStringAttribute("type");
            // awful patch to keep backward
            if ("VENDOR".equals(type)) {
                type = "DB_VENDOR";
            }
            Properties properties = context.getChildrenAsProperties();
            databaseIdProvider = (DatabaseIdProvider)resolveClass(type).newInstance();
            databaseIdProvider.setProperties(properties);
            Environment environment = configuration.getEnvironment();
            String databaseId =  databaseIdProvider.getDatabaseId(environment.getDataSource());
            configuration.setDatabaseId(databaseId);
        }

    }

    /**
     * <typeHandlers>
     *   <typeHandler handler="org.mybatis.example.ExampleTypeHandler"/>
     * </typeHandlers>
     * @param context
     */
    private void typeHandlerElement (XNode context) {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        if (context != null) {
            for (XNode child : context.getChildren()) {
                if ("package".equals(child.getName())) {
                    String typeHandlerPackage = child.getStringAttribute("name");
                    typeHandlerRegistry.register(typeHandlerPackage);
                } else {
                    String javaTypeName = child.getStringAttribute("javaType");
                    String jdbcTypeName = child.getStringAttribute("jdbcType");
                    String handlerTypeName = child.getStringAttribute("handler");
                    Class<?> javaTypeClass = resolveClass(javaTypeName);
                    JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
                    Class<?> typeHandlerClass = resolveClass(handlerTypeName);
                    if (javaTypeClass != null) {
                        if (jdbcType == null) {
                            // 使用类型处理器
                            typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
                        } else {
                            // 使用jdbc-type
                            typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
                        }
                    } else {
                        typeHandlerRegistry.register(typeHandlerClass);
                    }
                }
            }
        }
    }

    /**
     * <!-- 使用相对于类路径的资源引用 -->
     * <mappers>
     *   <mapper resource="org/mybatis/builder/AuthorMapper.xml"/>
     *   <mapper resource="org/mybatis/builder/BlogMapper.xml"/>
     *   <mapper resource="org/mybatis/builder/PostMapper.xml"/>
     * </mappers>
     * <!-- 使用完全限定资源定位符（URL） -->
     * <mappers>
     *   <mapper url="file:///var/mappers/AuthorMapper.xml"/>
     *   <mapper url="file:///var/mappers/BlogMapper.xml"/>
     *   <mapper url="file:///var/mappers/PostMapper.xml"/>
     * </mappers>
     * <!-- 使用映射器接口实现类的完全限定类名 -->
     * <mappers>
     *   <mapper class="org.mybatis.builder.AuthorMapper"/>
     *   <mapper class="org.mybatis.builder.BlogMapper"/>
     *   <mapper class="org.mybatis.builder.PostMapper"/>
     * </mappers>
     * <!-- 将包内的映射器接口实现全部注册为映射器 -->
     * <mappers>
     *   <package name="org.mybatis.builder"/>
     * </mappers>
     * @param context
     */
    private void mapperElement (XNode context) throws Exception {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        if (context != null) {
            for (XNode child : context.getChildren()) {
                if ("package".equals(child.getName())) {
                    String mapperPackage = child.getStringAttribute("name");
                    configuration.addMappers(mapperPackage);
                } else {
                    String resource = child.getStringAttribute("resource");
                    String url = child.getStringAttribute("url");
                    String mapperClass = child.getStringAttribute("class");
                    if (resource != null && url == null && mapperClass == null) {
                        ErrorContext.instance().resource(resource);
                        InputStream inputStream = Resources.getResourceAsStream(resource);
                        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                        mapperParser.parse();
                    } else if (resource == null && url != null && mapperClass == null) {
                        ErrorContext.instance().resource(url);
                        InputStream inputStream = Resources.getUrlAsStream(url);
                        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
                        mapperParser.parse();
                    } else if (resource == null && url == null && mapperClass != null) {
                        Class<?> mapperInterface = Resources.classForName(mapperClass);
                        configuration.addMapper(mapperInterface);
                    } else {
                        throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
                    }
                }
            }
        }
    }

    private boolean isSpecifiedEnvironment (String id) {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        if (environment == null) {
            throw new BuilderException("No environment specified.");
        } else if (id == null) {
            throw new BuilderException("Environment requires an attribute");
        } else {
            return environment.equals(id);
        }
    }

    private TransactionFactory transactionManagerElement(XNode context) throws Exception {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties props = context.getChildrenAsProperties();
            TransactionFactory factory = (TransactionFactory)resolveClass(type).newInstance();
            factory.setProperties(props);
            return factory;
        }
        throw new BuilderException("Environment declaretion requires a TransactionFactory.");
    }

    private DataSourceFactory dataSourceElement (XNode context) throws Exception {
        logger.info("im.lincq.mybatisplus.taste.MybatisXMLConfigBuilder#dataSourceElement");
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties properties = context.getChildrenAsProperties();
            DataSourceFactory factory = (DataSourceFactory)resolveClass(type).newInstance();
            factory.setProperties(properties);
            return factory;
        }
        throw new BuilderException("Environment declareation requires a DataSourceFactory.");
    }
}
