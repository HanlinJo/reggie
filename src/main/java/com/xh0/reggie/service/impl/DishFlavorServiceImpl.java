package com.xh0.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xh0.reggie.dto.DishDto;
import com.xh0.reggie.entity.DishFlavor;
import com.xh0.reggie.mapper.DishFlavorMapper;
import com.xh0.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

}
