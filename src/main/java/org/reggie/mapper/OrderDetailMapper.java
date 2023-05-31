package org.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.reggie.pojo.OrderDetail;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
