package org.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggie.common.CustomException;
import org.reggie.mapper.CategoryMapper;
import org.reggie.pojo.Category;
import org.reggie.pojo.Dish;
import org.reggie.pojo.Setmeal;
import org.reggie.service.CategoryService;
import org.reggie.service.DishService;
import org.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 判断关联 后根据id删除分类
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 查询是否已经关联菜品的数量，如数量>0抛出异常
        LambdaQueryWrapper<Dish> dishLQW = new LambdaQueryWrapper<>();
        dishLQW.eq(Dish::getCategoryId, id);
        int countDish = dishService.count(dishLQW);
        if (countDish > 0) {
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        // 查询是否已经关联套餐，如有抛出异常（异常业务类
        LambdaQueryWrapper<Setmeal> stmlLQW = new LambdaQueryWrapper<>();
        stmlLQW.eq(Setmeal::getCategoryId, id);
        int countSetmeal = setmealService.count(stmlLQW);
        if (countSetmeal > 0) {
            // 显示到页面的异常信息，由全局异常处理器捕获
            throw new CustomException("当前分类下关联了套餐，不能删除");

        }

        // 没有关联，继续删除分类
        super.removeById(id);
    }
}
