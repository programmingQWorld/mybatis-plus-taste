[2016-06]
taste.No1中，发现这里面并没有直接使用到Mybatis框架.
功能代码中，完成了通用简单的 crud 模式.

---

[2016-06]
taste.No2 中，作者向项目添加了类IdWorker.,具有ID生成功能.
eg: 生成唯一ID3122830616010162176
这东西也许就是雪花id了吧.看不懂这种，但应该没有太大影响.
不能抠得太细了，放弃这部分的理解.

---

[2016-6] [20190121]
taste.No3 中，发生了较大的变化.
mybatis-plus技术框架的雏形渐渐出现，正逐步开始有通用CRUD的结构产生.
所以本次角逐，应该好好体会其中抽象的过程以及用到的知识点.
<br>
这一回的更新中，删去了许多原有的文件，我可能得做一次版本首先.
新增annotation包，下有自定义注解 Id，Table.
<br>
新增generator包，下有auto-generator.方式，最新版本中式能够配置生成 Entity,Service/Impl, Mapper等等文件的.不知在最初版本中表现如何.后面再看。
<br>
新增通用Mapper接口和SQL注入器.在Mybatis-plus中有sql注入器的介绍.
SQL注入器依赖了Configuration，MapperBuilderAssistant两个类，这两个类出自Mybatis，但实际使用的是其子类.
<br>
感觉前面将 SimpleMapper类文件（通用crud逻辑）以及各个Handler(sql语句handler)，Processor(处理rs 转换为 bean)删除掉是因为 Mybatis 框架已经有这类功能了，无需重复编写. 所以对这些逻辑处理有兴趣的话可以参考mybatis框架的实现.在plus中应该不会再有了.
<br>
-1150-看到一种风格,主要逻辑方法写在类文件靠前部分，其引用的方法封装放在主要逻辑方法的后面部分. <br/>

##### 反射知识点

    getGenericInterfaces方法: 获取java对象所实现的接口.该方法的返回类型是 type\[\] <br/>
    ParameterizedType类以及getRawType，getActualTypeArguments方法 <br/>
    getActualTypeArguments方法: 返回Type对象的数组，表示此对象的实际类型参数(type args，指的应该就是泛型规约). 注意在某些情况下，返回的数组为空，比如类型无泛型约束     <br/>
    该方法返回参数化类型<>中的实际参数类型， 如 Map<String,Person> map 这个 ParameterizedType 返回的是 String 类,Person 类的全限定类名的 Type Array。注意: 该方法只返回最外层的<>中的类型，无论该<>内有多少个<>。 <br/>
getRawType(): Type
该方法的作用是返回当前的ParameterizedType的类型。如一个List，返回的是List的Type，即返回当前参数化类型本身的Type。


##### MyBatis 知识点
KeyGenerator 类
NoKeyGenerator 类
SqlSource 类
RawSqlSource 类
SqlCommandType 类

---

[2016-6][20190304][f1c8ec42942de0974f2915f3c2abf3b059e21355]

这次的taste中，能感受到的是抽取出TableInfo数据结构。 <br>
封装更多的细节是提供了更简明的调用方式。 <br>

经过这次尝试，了解了一个SQL的注入过程，注入方式。mybatis-plus通过为每一个 mapperClass 注入常用的sql语句，增强 mapper-class 的能力。<br>
```java
new AutoSqlInject().inject();
```

这次运行测试用例也遇到了一点点困难，首先是 xxxMapper.xml 在运行时target目录中是看不到的，导致最后 xx.getClassLoader().getResourceAsStream("xxx.xml")的方法获取不到对应的inputStream.
我一时不了解为什么IDEA编译后，文件没有被复制过去，我只好手动复制UserMapper.xml到target目录下，问题暂时得到解决。
<br>
其它的问题是自己的编码问题，注入的sql语句有语法问题。解决方式是到注入位置，把相关sql语句输出查看，就可以很快对比出错误。
<br>
也了解到 MyBatis 框架转化配置文件信息到Configuration对象的过程.

---
[201903041638]

这次版本解决正常添加指定主键对象<br>
```TableId```注解,若设置为false,需要用户传入ID。<br>
用户传入id可以使用```IdWorker```产生或者指定其它的ID.<br>

---
[201903041652]

这次的主题是分页插件，使用sql拦截，重写sql的方式来实行```物理分页```，非内存分页。
<br>
下面的资料来自mybatis-plus的```Readme.md```文件。

# MyBatis 分页插件

> pagination 是一个简易的MyBatis物理分页插件，使用`org.apache.ibatis.session.RowBounds`及其子类作为分页参数，禁用MyBatis自带的内存分页。

## 分页原理
简单来说就是通过拦截StatementHandler重写sql语句，实现数据库的物理分页

## 如何使用分页插件
**mybatis** 配置文件中配置插件 [mybatis-config.xml]
```
	<plugins>
	    <!--
	     | 分页插件配置
	     | 插件提供二种方言选择：1、默认方言 2、自定义方言实现类，两者均未配置则抛出异常！
	     | dialectType 数据库方言
	     |             默认支持  mysql  oracle  hsql  sqlite  postgre
	     | dialectClazz 方言实现类
	     |              自定义需要实现 com.baomidou.mybatisplus.plugin.pagination.IDialect 接口
	     | -->
	    <!-- 配置方式一、使用 MybatisPlus 提供方言实现类 -->
	    <plugin interceptor="com.baomidou.mybatisplus.plugins.PaginationInterceptor">
	        <property name="dialectType" value="mysql" />
	    </plugin>
	    <!-- 配置方式二、使用自定义方言实现类 -->
	    <plugin interceptor="com.baomidou.mybatisplus.plugins.PaginationInterceptor">
	        <property name="dialectClazz" value="xxx.dialect.XXDialect" />
	    </plugin>
	</plugins>
```

---
[201903081426]
分页对象Pagination继承了RowBounds类.实现了自定义的分页模型。<br>
在分页拦截器中，拦截器拦截了 StatementHandler 接口实例的 ```prepare``` 方法.<br>
拦截器首先是根据配置 dbtype 或者 dialectClazz 属性来决定组装分页语句的IDialect实例.<br>
在 dialectClazz明确情况下，可直接用new创建，否则根据dbtype到DialectFactory中获取相应方言处理分页的实例。<br>
从StatementHandler接口实例中获取 RowBounds 实例，该实例其实就是我们自定义的分页对象 Pagination 实例。通过它可以查看是否要做总数量查询.该实例目前不存储结果数据集。<br>
从StatementHandler接口实例中获取 BoundSql 实例，BoundSql实例里面可以找到被拦截到的sql语句，该语句是不包含分页语法的。
<br>
把分页插件部分啃了一遍，它的执行流程自己是明白的.<br>
但是，我还没看到有关这方面的处理：<br>
解决语句有时需要分页，有时不需要分页的问题.
也许在后面的taste中会发现。

<br>

---
[201903112107]
刚刚发现，Mybatis-plus框架不设置原始的资源Resources目录，而是在test下的Resources目录充当.
这样说明目前为止，框架自用的资源属于测试中使用。
[201903112139]
分支号: 3b6632196b4424cd8f46a8ab67669776c12a2599
<p>
这次分支，主要内容对test测试目录下的一些结构层次调整，mybatis-config.xml中加上了分页plugin配置，以及对一些配置文件知识点加以注释。
<br>
没有大的文件变化。但是我发现，加上plugin配置之后，test用例方法跑不动了.这时应该看看mybatis加载plugins标签时候，发生了什么事情。不，先仔细看一下配置文件plugins是否配置错了。
<p>
调试之后的发现：加载plugin标签时候，会按照配置的拦截器类去 alias 缓存中获取缓存 Class 实例，如果该实例不存在，直接使用反射方式获取拦截器类的 Class实例。接着根据拦截器Class实例实例化处拦截器实例，设置属性值是直接将属性对象传递给拦截器实例的setProperties方法，完成值的设置。
关键的地方便在于此，我漏写了setProperties，plugin等方法的实现代码，成了空实现，或许这就是异常原因。但是再仔细一想，空实现的方法那也一样是方法，方法内一行代码都没有，那怎么可能引起异常呢，显然不是这种原因。再往下debug调试.真正的原因即将浮出水面。

```Java
configuration.getInterceptors().add(interceptorInstance);
```
上面这句代码引起了java.lang.UnsupportedOperationException
```
Caused by: java.lang.UnsupportedOperationException
	at java.util.Collections$UnmodifiableCollection.add(Collections.java:1055)
	at im.lincq.mybatisplus.taste.MybatisXmlConfigBuilder.pluginElement(MybatisXmlConfigBuilder.java:206)
```

java.util.Collections$UnmodifiableCollection.add，我需要百度一下：

<p>

>Collections.unmodifiableCollection这个可以得到一个集合的镜像，它的返回结果不可直接被改变，否则会提示。
为的就是保护数据不要被改变。另外，修改原Collections时，会同时修改对应的镜像。

<p>

我需要再看一下更详细的实现，下面是configuration.getInterceptors()方法的实现，以及一个转机方法;

```
public void addInterceptor(Interceptor interceptor) {
    interceptors.add(interceptor);
  }

  public List<Interceptor> getInterceptors() {
    return Collections.unmodifiableList(interceptors);
  }
  ```

上面的方法实现跟百度的结果极其对应，所以根本原因就是操作了不可修改的集合镜像，异常。
<br>
我认为此时的救命稻草就是上面的转机方法（```addInterceptors方法```），因为它直接操作 interceptors 集合，不会出现问题，试一试吧。
然后就去睡觉.
<br>
哈哈，问题果然解决了.虽然现在又遇到了其它的NPE，但是已经不相关了，明天找时间再看看。明天也要补全漏写的方法。
<p>

本次配置的是```分页拦截器```，按照 mybatis 的约定，自定义的拦截器类都实现了 ```org.apache.ibatis.plugin.Interceptor``` 接口。

后面打算用文档docx来记录，现在不习惯使用markdown。