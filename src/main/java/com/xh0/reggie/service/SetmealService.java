package com.xh0.reggie.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xh0.reggie.dto.DishDto;
import com.xh0.reggie.dto.SetmealDto;
import com.xh0.reggie.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public void removeWithDish(List<Long> ids);
    public void updateWithDish(SetmealDto setmealDto);
    public SetmealDto getByIdWithDish(Long id);
}
