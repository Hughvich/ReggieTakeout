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
### login登录
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
  
# 放弃原项目，重新按照教程一五一十做

----

以下为重做项目：
- 做法区别：jdk用1.8，maven项目创建，按照教程引入pom和application.yml，数据库从idea里通过执行db_reggie.sql创建
- 成功完成以上login功能

---
### 后台退出功能
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
### 新增员工
- 后台系统管理信息，新增员工添加后台系统用户，点击按钮【添加员工】跳转到新增员工页面，
- 数据表中employee表的username添加唯一字段约束，status默认为1，账号停用是0
- 执行过程：
  - 页面发送ajax（axios）请求POST，将新增员工页面中输入的数据以json格式提交到服务器
  - 服务端controller接受页面提交的数据并调用service将数据进行保存
  - service调用mapper操作数据库保存数据
  - 唯一username异常：全局异常捕获类
