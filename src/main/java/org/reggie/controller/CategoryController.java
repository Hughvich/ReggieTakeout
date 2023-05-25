package org.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.R;
import org.reggie.pojo.Category;
import org.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 根据id删除分类
     * @param ids ？？？
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        log.info("删除分类，id：{}",ids);
        // 判断当前分类是否已经关联菜品/套餐
        categoryService.removeById(ids);
        return R.success("分类删除成功");
    }
}
