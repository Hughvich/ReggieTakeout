package org.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggie.mapper.EmployeeMapper;
import org.reggie.pojo.Employee;
import org.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

// ServiceImpl为mybatis plus的一个父类
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
