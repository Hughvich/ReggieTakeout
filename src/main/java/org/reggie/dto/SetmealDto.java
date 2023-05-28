package org.reggie.dto;


import lombok.Data;
import org.reggie.pojo.Setmeal;
import org.reggie.pojo.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
