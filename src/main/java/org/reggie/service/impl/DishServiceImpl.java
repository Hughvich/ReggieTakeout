package org.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.reggie.dto.DishDto;
import org.reggie.mapper.DishMapper;
import org.reggie.pojo.Dish;
import org.reggie.pojo.DishFlavor;
import org.reggie.service.DishFlavorService;
import org.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
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
    @Transactional //新增菜品，多张表操作，加入事务控制，在启动类开启事务
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表[dish]
        this.save(dishDto);

        // 保存菜品的口味信息到口味表[dish_flavor]，
        // dishDto.getFlavors()拿到的List集合中没dishId，应该把id通过dishDto.getId()赋值
        List<DishFlavor> flavorList = dishDto.getFlavors();
        flavorList = flavorList.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());//collect方法转成List
        // 批量保存
        dishFlavorService.saveBatch(flavorList);
    }

    /**
     * 查询根据id菜品及口味，关联查询dish_flavor
     * @param id
     */
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息dish ，拷到dishDto
        Dish dish = this.getById(id); // = dishService.getById(id)

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        // 查询菜品对应口味信息flavor，根据id查询，从dish_flavor查询,拿到口味列表flavors给到dishDto
        // dishDto = dish + flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新/修改 菜品及口味，
     * 口味相当于删除原有的，保存新传来的
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表的普通属性
        this.updateById(dishDto);
        // 清理当前菜品口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 插入提交新的口味数据到dish_flavor口味表
        // 和没有封装dishId，和新增菜品一样遍历setId
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
