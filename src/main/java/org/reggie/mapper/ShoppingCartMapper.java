package org.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.reggie.pojo.ShoppingCart;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
