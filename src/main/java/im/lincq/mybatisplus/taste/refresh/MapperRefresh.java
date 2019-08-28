package im.lincq.mybatisplus.taste.refresh;

import im.lincq.mybatisplus.taste.MybatisConfiguration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author lincq
 * @date 2019/8/28 09:39
 */
public class MapperRefresh implements Runnable {
    protected final Logger logger = Logger.getLogger(MapperRefresh.class.getName());
    private SqlSessionFactory sqlSessionFactory;
    private Resource[] mapperLocations;
    private Long beforeTime = 0L;
    private Configuration configuration;

    /**
     * 是否开启刷新mapper
     */
    private boolean enabled;

    /**
     * xml文件目录
     */
    private Set<String> fileSet;

    /**
     * 延迟加载时间
     */
    private int delaySeconds = 10;
    /**
     * 刷新间隔时间
     */
    private int sleepSeconds = 20;

    /**
     * 记录jar包存在的mapper
     */
    private static Map<String, List<Resource>> jarMapper = new HashMap<>();

    public MapperRefresh(Resource[] mapperLocations, SqlSessionFactory sqlSessionFactory, int delaySeconds, int sleepSeconds, boolean enabled) {
        this.mapperLocations = mapperLocations;
        this.sqlSessionFactory = sqlSessionFactory;
        this.delaySeconds = delaySeconds;
        this.enabled = enabled;
        this.sleepSeconds = sleepSeconds;
        this.run();  // 并没有使用新的线程
    }

    public MapperRefresh(Resource[] mapperLocations, SqlSessionFactory sqlSessionFactory, boolean enabled) {
        this.mapperLocations = mapperLocations;
        this.sqlSessionFactory = sqlSessionFactory;
        this.enabled = enabled;
        this.run();
    }

    /**
     * 在里面创建了线程 "mybatis-plus MapperRefresh"
     * 先延迟，然后在循环中，通过休眠达成每5s后执行下一次循环。
     */
    @Override
    public void run() {
        beforeTime = System.currentTimeMillis();
        if (enabled) {
            final MapperRefresh runnable = this;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (fileSet == null) {
                        fileSet = new HashSet<String>();
                        for (Resource mapperLocation : mapperLocations) {
                            try {
                                if (ResourceUtils.isJarURL(mapperLocation.getURL())) {
                                    // jarUrl 需要多一步骤处理
                                    // ? key的意义是 文件路径
                                    String key = new UrlResource(ResourceUtils.extractJarFileURL(mapperLocation.getURL())).getFile().getPath();
                                    fileSet.add(key);
                                    // 对jarMapper缓存的操作
                                    if (jarMapper.get(key) != null) {
                                        // 主逻辑
                                        jarMapper.get(key).add(mapperLocation);
                                    } else {
                                        List<Resource> resourcesList = new ArrayList<Resource>();
                                        resourcesList.add(mapperLocation);
                                        jarMapper.put(key, resourcesList);
                                    }
                                } else {
                                    // # 加入文件 set集合
                                    System.out.println("runnable # run: " + mapperLocation.getFile().getPath());
                                    fileSet.add(mapperLocation.getFile().getPath());
                                }
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }

                        /* 延迟 10 s 部分 */
                        try {
                            Thread.sleep(delaySeconds * 1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }

                        while (true) {
                            try {

                                for (String filePath : fileSet) {

                                    // 区分了 jar 包中的 mapper 和 本次工程中的mapper
                                    File file = new File(filePath);
                                    if (file.isFile() && file.lastModified() > beforeTime) {
                                        MybatisConfiguration.IS_REFRESH = true;
                                        List<Resource> removeList = jarMapper.get(filePath);
                                        //如果是jar包中的xml，将刷新jar包中存在的所有xml，后期再修改加载jar中修改过后的xml
                                        if (removeList != null && !removeList.isEmpty()) {
                                            for (Resource resource:removeList) {
                                                runnable.refresh(resource);
                                            }
                                        } else {
                                            runnable.refresh(new FileSystemResource(file));
                                        }
                                    }
                                }

                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }

                            /* 线程 休眠 sleepSeconds  部分 */
                            try {
                                Thread.sleep(sleepSeconds * 1000);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                    }
                }
            }, "mybatis-plus MapperRefresh").start();
        }
    }

    /**
     * 刷新mapper
     * @throws Exception exception
     */
    private void refresh(Resource resource) throws Exception {
        this.configuration = sqlSessionFactory.getConfiguration();
        boolean isSuper = configuration.getClass().getSuperclass() == Configuration.class;
        resource = new FileSystemResource(resource.getFile());
        try {
            Field loadedResourcesField = isSuper ? configuration.getClass().getSuperclass().getDeclaredField("loadedResources") : configuration.getClass().getDeclaredField("loadedResources");
            loadedResourcesField.setAccessible(true);
            Set loadedResourceSet = (Set)loadedResourcesField.get(configuration);
            XPathParser  xPathPaser = new XPathParser(resource.getInputStream(), true, configuration.getVariables(), new XMLMapperEntityResolver());
            XNode context = xPathPaser.evalNode("/mapper");
            String namespace = context.getStringAttribute("namespace");

            Field field = MapperRegistry.class.getDeclaredField("knownMappers");
            field.setAccessible(true);
            Map mapConfig = (Map) field.get(configuration.getMapperRegistry());
            mapConfig.remove(Resources.classForName(namespace));

            loadedResourceSet.remove(resource.toString());
            configuration.getCacheNames().remove(namespace);
            cleanParameterMap(context.evalNodes("/mapper/parameterMap"), namespace);
            cleanResultMap(context.evalNodes("/mapper/resultMap"), namespace);
            cleanKeyGenerators(context.evalNodes("mapper/sql"), namespace);

            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource.getInputStream(), sqlSessionFactory.getConfiguration(), // 注入的sql先不进行处理
                    resource.toString(), sqlSessionFactory.getConfiguration().getSqlFragments());
            xmlMapperBuilder.parse();
            logger.info("refresh: " + resource + ", success ! ");
        } catch (Exception e) {
            throw new NestedIOException("Failed to parse mapping resource: " + resource + "", e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    /**
     * 清理parameterMap
     * @param list                 list
     * @param namespace  namespace
     */
    private void cleanParameterMap (List<XNode> list, String namespace) {
        for (XNode parameterMapNode: list) {
            String id = parameterMapNode.getStringAttribute("id");
            configuration.getParameterMaps().remove(namespace + "." + id);
        }
    }

    private void cleanResultMap (List<XNode> list, String namespace) {
        for (XNode resultMapNode : list) {
            String id = resultMapNode.getStringAttribute("id");
            configuration.getResultMapNames().remove(id);
            configuration.getResultMapNames().remove(namespace +"." + id);
        }
    }

    private void cleanKeyGenerators (List<XNode> list, String namespace) {
        for (XNode context : list) {
            String id = context.getStringAttribute("id");
            configuration.getKeyGeneratorNames().remove(id + SelectKeyGenerator.SELECT_KEY_SUFFIX);
            configuration.getKeyGeneratorNames().remove(namespace + "." + id + SelectKeyGenerator.SELECT_KEY_SUFFIX);
        }
    }

    /**
     * 清理sql节点缓存
     */
    private void cleanSqlElement (List<XNode> list, String namespace) {
        for (XNode context : list) {
            String id = context.getStringAttribute("id");
            configuration.getSqlFragments().remove(id);
            configuration.getSqlFragments().remove(namespace + "." + id);
        }
    }

}
