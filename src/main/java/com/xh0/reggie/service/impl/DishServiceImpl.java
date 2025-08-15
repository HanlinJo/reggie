package com.xh0.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xh0.reggie.common.CustomException;
import com.xh0.reggie.dto.DishDto;
import com.xh0.reggie.entity.Dish;
import com.xh0.reggie.entity.DishFlavor;
import com.xh0.reggie.entity.Setmeal;
import com.xh0.reggie.mapper.DishFlavorMapper;
import com.xh0.reggie.mapper.DishMapper;
import com.xh0.reggie.service.DishFlavorService;
import com.xh0.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors =  dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
    @Override
    public DishDto getByIdWithFlavor(Long id){
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
//    @Transactional
//    public void remove(List<Long> ids){
//
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.in(Dish::getId,ids);
//        lambdaQueryWrapper.eq(Dish::getStatus,1);
//        int count = this.count(lambdaQueryWrapper);
//        if(count>0){
//            throw new CustomException("菜品在售，无法删除");
//        }
//        this.removeByIds(ids);
//        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,ids);
//        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
//
//    }
    @Transactional
    public void removeWithDish(List<Long> ids){

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Dish::getId,ids);
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        int count = this.count(lambdaQueryWrapper);
        if(count>0){
            throw new CustomException("菜品在售，无法删除");
        }
        this.removeByIds(ids);
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

    }

}
