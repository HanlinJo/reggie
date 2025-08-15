package com.xh0.reggie.dto;

import com.xh0.reggie.entity.Setmeal;
import com.xh0.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
