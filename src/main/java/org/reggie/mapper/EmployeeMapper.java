package org.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.reggie.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

//BaseMapper为mybatis plus的一个父类，CRUD方法都有
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
