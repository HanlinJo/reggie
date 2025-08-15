package com.xh0.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xh0.reggie.dto.DishDto;
import com.xh0.reggie.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {
    //新增菜品，同时插入口味数据
    public void saveWithFlavor(DishDto dishDto);
    public void updateWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavor(Long id);
    public void removeWithDish(List<Long> ids);
}
