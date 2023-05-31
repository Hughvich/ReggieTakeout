package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.R;
import org.reggie.dto.DishDto;
import org.reggie.pojo.Category;
import org.reggie.pojo.Dish;
import org.reggie.pojo.DishFlavor;
import org.reggie.service.CategoryService;
import org.reggie.service.DishFlavorService;
import org.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto dish中的flavors没法接收，重新声明一个类，DTO-DataTransferObject，用于展示层和服务层之间的数据传输
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * ***************重 点 - 复 杂 点***************
     * 菜品分页查询
     * @param page
     * @param pageSize
     * @param name 查询框输入的菜品名
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 按name条件，模糊查询
        queryWrapper.like(name != null, Dish::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝,把pageInfo的除了records之外的其他属性拷给dishDtoPage
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");
        // 将pageInfo的records 处理{new一个Dto对象，对象拷贝把records的属性给它，
        // 并且赋予按照分类id对应的分类名称}后，给DishDto
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            // 要用到Dto，里面有个CategoryName属性
            DishDto dishDto = new DishDto();
            // item的其他普通属性拷贝给Dto
            BeanUtils.copyProperties(item, dishDto);
            // 拿到item的分类id
            Long categoryId = item.getCategoryId();
            // 查数据库（先判断category是否为空），拿到id对应的菜品 分类名称，赋给dishDto
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        // dishDto的集合对象给Page，进行分页
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品普通信息(dish)+口味(dish_flavor)
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        // 主要联合查询getByIdWithFlavor()在serviceImpl里完成
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("修改菜品：" + dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询 菜品
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 添加条件，查询状态status=1即在售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);


        List<DishDto> dishDtoList = list.stream().map((item) -> {
            // 要用到Dto，里面有个CategoryName属性
            DishDto dishDto = new DishDto();
            // item的其他普通属性拷贝给Dto
            BeanUtils.copyProperties(item, dishDto);
            // 拿到item的分类id
            Long categoryId = item.getCategoryId();
            // 查数据库（先判断category是否为空），拿到id对应的菜品 分类名称，赋给dishDto
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            // 口味flavor + item原属性 赋给dishDto
            LambdaQueryWrapper<DishFlavor> dishFlavorLQW = new LambdaQueryWrapper<>();
            dishFlavorLQW.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLQW);
            dishDto.setFlavors(dishFlavorList);

            // 以上口味flavor + item原属性 赋给dishDto 也可以用以下方法：
//            DishDto dishDto1 = dishService.getByIdWithFlavor(item.getId());
//            BeanUtils.copyProperties(item, dishDto1);

            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dishDtoList);
    }

    /**
     * *********用Dish实体类的list方法*********
     * 根据条件查询 菜品
     * @param dish
     * @return
     */
    /*
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        // 添加条件，查询状态status=1即在售状态的菜品
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }

     */


}
