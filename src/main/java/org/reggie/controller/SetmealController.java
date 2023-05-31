package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.R;
import org.reggie.dto.SetmealDto;
import org.reggie.pojo.Category;
import org.reggie.pojo.Setmeal;
import org.reggie.pojo.SetmealDish;
import org.reggie.service.CategoryService;
import org.reggie.service.SetmealDishService;
import org.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理，
 * 操作主表为setmeal表，所以Controller叫SetmealController
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，套餐表setmeal和套餐菜品关系表setmeal_dish
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("新增套餐：" + setmealDto);
        setmealService.saveWithDsih(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        // 页面 套餐分类 无法正常显示，用dto扩展的CategoryName
        Page<SetmealDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            // 普通属性拷给setmealDto
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            // 根据CategoryId查CategoryName
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        return R.success(dtoPage.setRecords(list));
    }

    /**
     * 删除套餐
     * @param ids 一个Long型的List
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("删除套餐：ids={}", ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 根据条件查询 套餐（用于移动端显示套餐）
     * @param setmeal 注意传过来的是key-value
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list( Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);

    }

}
