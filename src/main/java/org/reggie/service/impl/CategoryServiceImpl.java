package org.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.reggie.mapper.CategoryMapper;
import org.reggie.pojo.Category;
import org.reggie.service.CategoryService;
import org.reggie.service.DishService;
import org.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    private SetmealService setmealService;

    /**
     * 判断关联 后根据id删除分类
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 查询是否已经关联菜品，如有抛出异常

        // 查询是否已经关联套餐，如有抛出异常


    }
}
