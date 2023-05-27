package org.reggie.dto;

import org.reggie.pojo.Dish;
import lombok.Data;
import org.reggie.pojo.DishFlavor;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面传来的flavors无法和原实体类Dish对应，扩展flavors的List（里面是json）
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
