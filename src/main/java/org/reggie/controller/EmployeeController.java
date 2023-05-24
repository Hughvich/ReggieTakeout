package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.reggie.common.R;
import org.reggie.pojo.Employee;
import org.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录功能：
     *  传来username和password，json形式，封装成employee类
     *  登陆成功，员工对象的ID传给session一份，用到HttpServletRequest类获取session
     * @param employee
     * @param request 用于拿到session，拿到登录用户名
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        //   1. 页面提交的password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        // 2. 根据页面提交的username查数据库
            // 包装查询对象
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
            // 添加查询条件，等值查询eq
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
            // 查询数据库：username字段唯一约束，调用getOne查询唯一数据
        Employee emp = employeeService.getOne(queryWrapper);

        // 如果没查则返回失败结果
        if (emp == null) return R.error("登陆失败，用户名不存在");

        // 3. 密码比对,getPassword是原密码，password是所提交的密码（如果不符则返回失败）
        if(! emp.getPassword().equals(password))

        // 4. 查看员工锁定状态status=1（0为锁定）（如果已禁用则返回）
        if (emp.getStatus() == 0) return R.error("登陆失败，账号已禁用");

        // 5. 登陆成功，员工id存入session并返回成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 退出功能：
     *     清理Session中的用户id，返回结果
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    /**
     * 新增员工，返回code=1为添加成功
     * @param employee
     * @param request
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request) {
        log.info("新增员工：{}",employee.toString());
        // 设置初始密码，md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
        // 记录新增+修改时间，创建人(从session拿)，更新人
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser((Long)request.getSession().getAttribute("employee"));
        employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));

        // 调用IService[MB+]里的save方法，将实体类存进数据库
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 分页查询，请求参数：
     * @param page 一共多少页
     * @param pageSize 每页多少条
     * @param name 按名称的窗口查询
     * @return Page类为MB+封装的返回类，
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("分页查询：page={}, pageSize={}, name={}", page,pageSize,name);

        // 分页构造器的构造
        Page pageInfo = new Page(page, pageSize);

        // 条件构造器LambdaQueryWrapper的构造，过滤条件，当name字段isNotEmpty时，
        // 执行like模糊查询，根据Employee里的name字段
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        // 排序条件order by：
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);

    }

}
