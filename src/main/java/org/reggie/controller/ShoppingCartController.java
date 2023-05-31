package org.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.BaseContext;
import org.reggie.common.R;
import org.reggie.pojo.ShoppingCart;
import org.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 加购：菜品/套餐加入购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("加购：" + shoppingCart);
        // 设置当前用户ID
        shoppingCart.setUserId(BaseContext.getCurrentId());

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        // 菜品/套餐判断：如果dishId 存在 则是菜品
         if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
             //否则为null 是setmealId 套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
         // select * from shopping_cart where user_id = ? and dish_id = ? / setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

         // 同一菜品点多次，数据库里计次数的参数number+1
        // 查询是否已经有该菜品，已经有则 number+1
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            // 更新查询
            shoppingCartService.updateById(cartServiceOne);
        } else {
            // 否则还没有该菜品，加购，数量number为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    /**
     * 按userid条件查询，购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        return R.success(shoppingCartService.list(queryWrapper));
    }

    /**
     * 减号按钮，按dish/setmeal的Id删除菜品
     * @param shoppingCart 封装dishId或setmealId
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> update(@RequestBody ShoppingCart shoppingCart) {
        log.info("按照菜品/套餐id修改/删除：" + shoppingCart);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        // 判断是菜品还是套餐
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        // 数量为2+的点一次减号 删一个
        // 查询菜品是否有1个以上，已经有则 number-1
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne.getNumber() > 1) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number - 1);
            // 更新查询
            shoppingCartService.updateById(cartServiceOne);
        } else {
            // 否则菜品只有一个，直接删掉
            shoppingCartService.remove(queryWrapper);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }
    /**
     * 按userid清空购物车,全部清空
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        log.info("清空购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }
}
