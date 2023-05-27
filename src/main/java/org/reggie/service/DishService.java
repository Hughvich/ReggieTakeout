package org.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.reggie.dto.DishDto;
import org.reggie.pojo.Dish;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对应的口味数据dishFlavor，操作dish和dish_flavor两个表
    public void saveWithFlavor(DishDto dishDto);

}
