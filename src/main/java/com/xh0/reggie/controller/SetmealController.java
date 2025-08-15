package com.xh0.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh0.reggie.common.CustomException;
import com.xh0.reggie.common.R;
import com.xh0.reggie.dto.DishDto;
import com.xh0.reggie.dto.SetmealDto;
import com.xh0.reggie.entity.Category;
import com.xh0.reggie.entity.Setmeal;
import com.xh0.reggie.mapper.SetmealDishMapper;
import com.xh0.reggie.service.CategoryService;
import com.xh0.reggie.service.DishService;
import com.xh0.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息:{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.hasText(name),Setmeal::getName,name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,lambdaQueryWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category != null){
                setmealDto.setCategoryName(category.getName());
            }
        return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
//        return null;
    }
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }
    @PostMapping("/status/0")
    public R<String> updateStatusStop(@RequestParam List<Long> ids) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.in(Setmeal::getStatus,0);
        int count = setmealService.count(lambdaQueryWrapper);
        if(count>0){
            throw new CustomException("有处于停售状态的套餐，无法一键停售");
        }
        for(Long id:ids){
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(0);
            LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
            queryWrapper.eq(Setmeal::getId ,id);

            setmealService.update(setmeal,queryWrapper);
        }
        return R.success("批量停售成功");
    }

    @PostMapping("/status/1")
    public R<String> updateStatusStart(@RequestParam List<Long> ids) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.in(Setmeal::getStatus,1);
        int count = setmealService.count(lambdaQueryWrapper);
        if(count>0){
            throw new CustomException("有处于起售状态的套餐，无法一键起售");
        }
        for(Long id:ids){

            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(1);
            LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
            queryWrapper.eq(Setmeal::getId , id);

            setmealService.update(setmeal,queryWrapper);
        }
        return R.success("批量起售成功");
    }
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        log.info(id.toString());

        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody SetmealDto setmealDto) {
        log.info(setmealDto.toString());

        setmealService.updateWithDish(setmealDto);
        return R.success("菜品信息修改成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,1);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }
}
