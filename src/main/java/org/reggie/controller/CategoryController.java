package org.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.R;
import org.reggie.pojo.Category;
import org.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
分类管理：菜品管理，套餐管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类：菜品/套餐
     * @param category 封装name，type，sort
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        categoryService.save(category);
        log.info("新增类型{}",category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);
        // 条件构造器，升序排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);

        // 用MB+的page方法分页查询
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);

    }

    /**
     * 根据id删除分类，删除前判断当前分类是否已经关联菜品/套餐
     * @param ids 本是id，前端传来的就是ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        log.info("删除分类，id：{}",ids);
        // 判断当前分类是否已经关联菜品/套餐
        categoryService.remove(ids);
        return R.success("分类删除成功");
    }

    /**
     * 根据id修改category分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改分类信息：{}", category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 根据条件查询分类（用于菜品分类下拉列表查询）
     * @param category 用其中的type，按type查分类列表
     * @return list 菜品分类的下拉列表，返回一个list
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 添加条件，根据type查询，先判断type不为空
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // 添加排序，如果排序号相同，根据修改时间降序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }


}
