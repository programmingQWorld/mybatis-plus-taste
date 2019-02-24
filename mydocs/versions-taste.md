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

