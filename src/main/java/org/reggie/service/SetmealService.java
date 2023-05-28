package org.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.reggie.dto.SetmealDto;
import org.reggie.pojo.Dish;
import org.reggie.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    // 新增 带有菜品关联的套餐
    void saveWithDsih (SetmealDto setmealDto);
    // 删除套餐，关联的Dish一起
    void removeWithDish(List<Long> ids);
}
