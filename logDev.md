# 记录项目开发过程
瑞吉外卖项目by it黑马，5/20 开始

## 搭建环境

### 搭建数据库：
遇到密码错误问题，已解决1234

---

### pom和application.yml配置文件：
如资料

---

### @springbootapplication
(exclude={DataSourceAutoConfiguration.class}) 排除自动注入数据源的配置（取消数据库配置）

### 静态资源映射：
配置类WebMvcConfig继承WebMvcConfigurationSupport,重写其中方法addResourceHandlers，
用于设置静态资源映射，如网页请求index.html，映射到resource/backend/index.html

---
## login登录
准备实体类pojo/Employee，三层架构， 基于mybatis plus[mb]：
- @Mapper接口继承mb的BaseMapper<Employee>，实现CRUD，
- EmpService接口继承mb的IService<Employee>，
  实现类EmployeeServiceImpl@Service 继承mb的ServiceImpl<EmployeeMapper, Employee>，实现EmpService接口
- EmpController @RestController类，
- ***login方法处理逻辑：
  1. 页面提交的password进行md5加密
  2. 根据页面提交的username查数据库（如果没有则返回失败）
  3. 密码比对（如果不符则返回失败） 
  4. 查看员工锁定状态status=1（0为锁定）（如果已禁用则返回）
  5. 登陆成功，员工id存入session并返回成功结果

- 登录login方法执行时遇到问题：
  Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.ahun.reggietakeout.mapper.EmployeeMapper' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {@org.springframework.beans.factory.annotation.Autowired(required=true)} 
  - 解决：无法解决
  

---
# -------放弃原项目，重新按照教程一五一十做--------

----

以下为项目重新搭建：
- 做法区别：jdk用1.8，maven项目创建，按照教程引入pom和application.yml，数据库从idea里通过执行db_reggie.sql创建
- 成功完成以上login功能

---
### 登录管理 - 后台退出功能
登录页面(backend/page/login/login.html)登录成功后跳转到/backend/index.html首页面，显示登陆用户名，  
- 退出系统功能通过用户名右侧退出按钮实现，退出后跳回登陆页面
- 发送退出请求的地址是/employee/logout，请求方式POST
- 在controller中创建对应方法：

- 完善登录功能：用过滤器/拦截器，登录成功后才能访问首页index.html   
  过滤器逻辑：
  - 获取本次请求URI
    （URL和URI关系：URL是一种URI（子集），最流行的资源标识（即地址））
  - 判断本次请求是否需要处理，否则放行
  - 判断登录状态，已登录则放行
  - 未登录则返回未登录结果

---
## 员工管理
### 新增员工
- 后台系统管理信息，新增员工添加后台系统用户，点击按钮【添加员工】跳转到新增员工页面，
- 数据表中employee表的username添加唯一字段约束，status默认为1，账号停用是0
- 执行过程：
  - 页面发送ajax（axios）请求POST，将新增员工页面中输入的数据以json格式提交到服务器
  - 服务端controller接受页面提交的数据并调用service将数据进行保存
  - service调用mapper操作数据库保存数据
  - 唯一username异常：全局异常捕获类

---
### 分页查询员工
- 分页查询：每页x条，前往第x页，输入框姓名查询
- 发送ajax请求，将分页查询参数（page，pageSize，name）提交服务端
- 服务端Controller接受页面提交的数据并调用Service，Service调用Mapper操作数据库，
- Controller将查询到的分页数据响应到页面
- 条件构造器LambdaQueryWrapper, name模糊条件查询，设置排序，调用MB+的分页查询方法page进行查询

---
### 启用/禁用员工账号
- 禁用账号，不能登录，启动账号，正常登录
- 管理员对所有员工账号进行操作：启用/禁用按钮，status=1 按钮显示禁用，status=0 按钮显示启用
- 普通用户只看到帐号状态，操作只有编辑，没有启/禁用

执行过程：
- 页面发送ajax请求，参数id和status（与原status相反）提交到服务端
- 服务端controller接收调用service，再调用mapper，对status字段进行 更新 操作

出现id精度丢失问题，即没有查到id，js对Long丢失最后两位精度（16位，而MB+雪花算法id自增位19位），需要转成字符串  
解决1：在实体类id上加注解 @TableId(value = "id",type = IdType.AUTO)
↑以上解决方法测试无用

解决2：对象转换器JacksonObjectMapper，基于Jackson进行Java对象和json数据的互相转换（Java->json 称为序列化过程）；  
在序列化过程中，通过ToStringSerializer将Long转为String  
在WebMvcConfig配置类中扩展SpringMVC的消息转换器，在消息转换器中使用提供的对象转换器进行java

---
### 编辑员工信息
执行流程：
- 页面上点击“编辑”按钮，页面跳转到 输入框页面add.html，传递url，携带参数：员工id
- 在add.html页面获取url中的参数：员工id
- 发送ajax请求，到服务端，传参员工id
- 服务端接收请求，根据员工id查询（page分页查询方法），将员工信息以json格式响应给输入框页面
- 输入框页面接收服务端响应json数据，通过vue的数据绑定进行员工信息回显
- 点击 保存 按钮，再次发送ajax请求到服务端，提交输入框页面中 修改后的 员工信息json
- 服务端接收员工信息并进行处理update方法，完成后响应给页面
- 页面接收服务端响应信息后进行相应处理

--- 
---

## 分类管理
### 分类 - 新增
- 菜品 对应-> 菜品分类  
- 套餐 对应-> 套餐分类  
- 分类管理页面中添加菜品分类，和套餐分类按钮，两个输入框
- 需要一个排序
- 数据表category中，类型type=1菜品分类，2套餐分类

新增分类执行过程：
  - 点击新增菜品/套餐分类按钮，输入框输入数据：分类名称name+排序sort+类型type， 
    页面发送ajax请求，将新增分类窗口输入的数据以json形式提交到服务端
  - 服务端Controller接收数据调用Service，Mapper，数据库保存数据

### 分类 - 分页查询
基本同员工页面查询

### 分类 - 删除
- 普通的删除，但是如果分类关联了菜品或套餐，分类不允许被删除
- 过程：点删除，ajax提交id到服务端，controller-service-mapper操作数据库删除(MB+的RemoveById方法)
- Dish实体类里的categoryId属性和分类Category的id关联，套餐类里的categoryId也是

### 分类 - 修改
操作-修改按钮，弹出“修改分类”输入框，框内自动回显信息（前端已实现），修改后点击确定按钮

---
### MB+功能：公共字段自动填充
公共字段，比如创建时间，创建人，修改时间，修改人，可被自动填充简化，指定字段赋予指定值  
实现：实体类属性上加注解@TableField，指定自动填充的策略  
    按照框架要求编写元数据对象处理器，在此类中统一为公共字段赋值，此类需要实现MetaObjectHandler接口
获取当前修改/创建人，使用到ThreadLocal类  
客户端每次发送http请求到服务端都是同一个新的线程，
从LoginCheckFilter的doFilter方法，到EmployeeController的update方法，再到MyMetaObjectHandler的updateFill方法，  
ThreadLocal是Thread的局部变量，为每个使用该变量的线程提供独立的变量副本，  
可以保存一些数据，线程隔离效果：线程内可以获取对应的值，线程外不可访问
实现步骤：
- 写BaseContext工具类，基于ThreadLocal封装
- LoginCheckFilter中的doFilter方法调用BC，设置当前用户id
- MyMetaObjHdl中调用BC获取用户id

---
---
## 菜品管理
### 菜品 - 文件上传
- upload，对于form表单，使用post方式，multipart格式，输入框类型type="file"
- 服务端接收客户端页面上传的文件，用到commons-fileupload和common-io两个apache组件
- 在controller中声明一个MultipartFile类的参数来接收上传的文件

### 菜品 - 新增
- 新增按钮弹出输入框，选择所属菜品分类，上传菜品图片，
- 涉及两个表：新增 - 将输入框信息插入到dish表，如果添加了口味做法，向dish_flavor插入数据，如甜味、温度、辣度
- 过程：
  - 首先页面发送ajax请求，查询把菜品分类显示在下拉框中
  - 上传菜品图片，页面发送请求-上传文件，服务端将图片保存到服务器
  - 下载图片（回显），页面发送请求-下载文件，响应回页面回显
  - 点击保存按钮，发送ajax请求，将菜品数据json提交服务端
  
页面dish中的flavors数据没法用Dish实体类接收（属性无法一一对应），重新声明一个类，DTO-DataTransferObject，用于展示层和服务层之间的数据传输

### 菜品 - 分页查询
- 除了菜品自己的基本信息外，还有展示图片，菜品分类，需要联表查询  
- 页面发送ajax请求，将分页查询参数page，pageSize，name（查询栏）提交服务端，以及图片下载

### 菜品 - 修改
交互过程：
- 页面发送 3个 ajax请求，获取菜品分类下拉框数据回显（新增菜品中已经实现），根据id查菜品回显，下载图片回显
- 点击保存按钮，发送ajax请求，修改后的菜品数据提交服务端（格式和新增菜品相同）

---
---
##套餐管理
- 菜品的集合：一份套餐包括多种菜品，多种菜品分类
- 数据表：setmeal，setmeal_dish套餐-菜品关系表，一个setmeal_id对应多个dish_id

### 套餐 - 新增
- 增加一份套餐，选择套餐分类（如商务套餐/儿童套餐），新增多个菜品，上传对应图片
交互过程/6次请求：
- 页面/backend/page/combo/add.html发送ajax请求，服务端从数据库获取 
- 套餐分类CategoryName展示在 ”套餐分类“下拉框（已在之前完成，在CategoryController.list()） 
- 菜品分类（已在之前完成，在CategoryController.list()）,
- 以及根据菜品分类的菜品数据展示到 “添加菜品窗口” 
- 图片上传 / 图片下载，回显 
- 点击保存，发送ajax，套餐数据json提交服务端

### 套餐 - 分页查询
交互过程：
- 页面发送ajax将分页参数page，pageSize，name提交到服务端获取分页数据；请求图片下载，回显

### 套餐 - 删除
- 操作：套餐管理 - 删除按钮 / 复选框 - 批量删除
- 售卖状态为启售的不能删除，需要先停售再删除
