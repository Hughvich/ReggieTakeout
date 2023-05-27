package org.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.reggie.dto.DishDto;
import org.reggie.mapper.DishMapper;
import org.reggie.pojo.Dish;
import org.reggie.pojo.DishFlavor;
import org.reggie.service.DishFlavorService;
import org.reggie.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     *
     * ************重 点 难 点*************
     * 新增菜品，保存附加口味flavor
     * @param dishDto
     */
    @Transactional //多张表操作，加入事务控制，在启动类开启事务
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表[dish]
        this.save(dishDto);

        // 保存菜品的口味信息到口味表[dish_flavor]，
        // dishDto.getFlavors()拿到的List集合中没id，应该把id通过dishDto.getId()赋值
        Long dishId = dishDto.getId();
        List<DishFlavor> flavorList = dishDto.getFlavors();
        flavorList = flavorList.stream().map((item) -> {
            item.setDishId(dishId);
            return item;                //为什么return？
        }).collect(Collectors.toList());//collect方法转成List
        // 批量保存
        dishFlavorService.saveBatch(flavorList);
    }
}
