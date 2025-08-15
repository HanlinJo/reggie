package com.xh0.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xh0.reggie.common.CustomException;
import com.xh0.reggie.dto.DishDto;
import com.xh0.reggie.dto.SetmealDto;
import com.xh0.reggie.entity.DishFlavor;
import com.xh0.reggie.entity.Setmeal;
import com.xh0.reggie.entity.SetmealDish;
import com.xh0.reggie.mapper.SetmealMapper;
import com.xh0.reggie.service.SetmealDishService;
import com.xh0.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Transactional
    public void saveWithDish(SetmealDto setmealDto){
        this.save(setmealDto);
        Long setmealId = setmealDto.getId();

        List<SetmealDish> setmealDishes= setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    public void removeWithDish(List<Long> ids) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);

    }
    @Override
    public void updateWithDish(SetmealDto setmealDto){
        this.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(list);
    }
    @Override
    public SetmealDto getByIdWithDish(Long id){
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list =  setmealDishService.list(lambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);
        return setmealDto;
    }


}
