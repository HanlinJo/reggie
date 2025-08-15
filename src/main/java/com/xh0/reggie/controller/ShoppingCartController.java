package com.xh0.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xh0.reggie.common.BaseContext;
import com.xh0.reggie.common.R;
import com.xh0.reggie.entity.ShoppingCart;
import com.xh0.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController{

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        log.info(shoppingCart.toString());
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<ShoppingCart>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if (dishId != null) {
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if(cartServiceOne != null){
            Integer number =  cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else{
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne =  shoppingCart;
        }
        return R.success(cartServiceOne);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> listShoppingCart() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        Long currentId = BaseContext.getCurrentId();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }
    @PostMapping("/sub")
    public R<String> subShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        log.info(shoppingCart.toString());
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        Long dishId = shoppingCart.getDishId();
        if (dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if(cartServiceOne.getNumber()>1){
            cartServiceOne.setNumber(cartServiceOne.getNumber()-1);
            shoppingCartService.updateById(cartServiceOne);
        }else{
            shoppingCartService.remove(queryWrapper);
        }
        return R.success("商品减少成功");
    }
    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
