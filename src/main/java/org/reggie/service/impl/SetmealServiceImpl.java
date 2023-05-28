package org.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.CustomException;
import org.reggie.dto.SetmealDto;
import org.reggie.mapper.SetmealMapper;
import org.reggie.pojo.Dish;
import org.reggie.pojo.Setmeal;
import org.reggie.pojo.SetmealDish;
import org.reggie.service.SetmealDishService;
import org.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    // 操作套餐-菜品关联关系
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增 带有菜品关联的套餐
     * @param setmealDto
     */
    @Override
    public void saveWithDsih(SetmealDto setmealDto) {
        // 保存套餐基本信息，操作setmeal表
        this.save(setmealDto);
        // 保存套餐-菜品关联信息，操作setmeal_dish表
        // SetmealDish中setmealId没有值，需要遍历通过setmealDto.getId()赋过去
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，关联的Dish一起
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，停售可删
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // select count(*) from setmeal where id in (1, 2, 3,...) and status = 1;
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        // 如果不能删，抛业务异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        // 如果可删 先删setmeal表中数据
        this.removeByIds(ids);
        // 再删关系表数据, 传过来的套餐setmeal ids不是关系表里的主键，要构建一个LQW，根据关系表的setmeal_id删
        // LQW = delete from setmeal_dish where setmeal_id in ids(1, 2, ...)
        LambdaQueryWrapper<SetmealDish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishQueryWrapper);
    }


}
