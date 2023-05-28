package org.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.reggie.dto.DishDto;
import org.reggie.pojo.Dish;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对应的口味数据dishFlavor，操作dish和dish_flavor两个表
    void saveWithFlavor(DishDto dishDto);
    // 根据id查菜品及口味，关联查询dish_flavor
    DishDto getByIdWithFlavor(Long id);
    // 修改菜品及口味，关联修改dish_flavor
    void updateWithFlavor(DishDto dishDto);
}
