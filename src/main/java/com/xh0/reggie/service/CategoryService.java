package com.xh0.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xh0.reggie.entity.Category;


public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
