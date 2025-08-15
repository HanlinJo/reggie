package com.xh0.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xh0.reggie.common.CustomException;
import com.xh0.reggie.entity.Category;
import com.xh0.reggie.entity.Dish;
import com.xh0.reggie.entity.Setmeal;
import com.xh0.reggie.mapper.CategoryMapper;
import com.xh0.reggie.service.CategoryService;
import com.xh0.reggie.service.DishService;
import com.xh0.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
//        查询当前分类是否关联菜品/套餐
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count>0){
            // 抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 =  setmealService.count(setmealLambdaQueryWrapper);
        if(count2>0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        super.removeById(id);

    }
}
