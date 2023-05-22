package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.R;
import org.reggie.pojo.Employee;
import org.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // 传来username和password，json形式，封装成employee类
    // 登陆成功，员工对象的ID传给session一份，用到HttpServletRequest类获取session

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

}
