package com.xh0.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh0.reggie.common.CustomException;
import com.xh0.reggie.common.R;
import com.xh0.reggie.dto.DishDto;
import com.xh0.reggie.entity.Category;
import com.xh0.reggie.entity.Dish;
import com.xh0.reggie.entity.DishFlavor;
import com.xh0.reggie.entity.Employee;
import com.xh0.reggie.mapper.DishFlavorMapper;
import com.xh0.reggie.service.CategoryService;
import com.xh0.reggie.service.DishFlavorService;
import com.xh0.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        String key = "dish_"+ dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        //分页构造器
        Page<Dish> pageInfo = new Page(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器(排序)
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        if (name != null) {
//            queryWrapper.like(Employee::getName,name);
//        }
//        if (!name.equals("admin")){
        queryWrapper.like(StringUtils.hasText(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list =records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        log.info(id.toString());

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody DishDto dishdto){
        log.info(dishdto.toString());

        dishService.updateWithFlavor(dishdto);
        String key = "dish_"+dishdto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("菜品信息修改成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("删除分类，id为：{}",ids);
        dishService.removeWithDish(ids);
//        categoryService.removeById(ids);
        return R.success("分类信息删除成功");
    }
    @PostMapping("/status/0")
    public R<String> updateStatusStop(@RequestParam List<Long> ids){
        log.info(ids.toString());
        LambdaQueryWrapper<Dish> queryWrapper =  new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,0);
        int count = dishService.count(queryWrapper);
        if (count>0){
            throw new CustomException("有处于停售状态的菜品，停售失败");
        }
        for(Long id:ids){
            Dish dish=dishService.getById(id);
            dish.setStatus(0);
            LambdaUpdateWrapper<Dish> lambdaQueryWrapper = new LambdaUpdateWrapper<>();
            lambdaQueryWrapper.eq(Dish::getId, id);
            dishService.update(dish, lambdaQueryWrapper);
        }

        return R.success("商品出售状态修改成功");
    }

    @PostMapping("/status/1")
    public R<String> updateStatusStart(@RequestParam List<Long> ids){
        log.info(ids.toString());
        LambdaQueryWrapper<Dish> queryWrapper =  new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        int count = dishService.count(queryWrapper);
        if (count>0){
            throw new CustomException("有处于启售状态的菜品，启售失败");
        }
        for(Long id:ids){
            Dish dish=dishService.getById(id);
            dish.setStatus(1);
            LambdaUpdateWrapper<Dish> lambdaQueryWrapper = new LambdaUpdateWrapper<>();
            lambdaQueryWrapper.eq(Dish::getId, id);
            dishService.update(dish, lambdaQueryWrapper);
        }
        return R.success("商品出售状态修改成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
//        List<Dish> categoryList = dishService.list(queryWrapper);
//        return R.success(categoryList);
//    }
@GetMapping("/list")
public R<List<DishDto>> list(Dish dish){
    List<DishDto> dishDtoList =null;
    String key = "dish_" + dish.getCategoryId()+"_"+dish.getStatus();
    dishDtoList= (List<DishDto>)redisTemplate.opsForValue().get(key);
    if (dishDtoList!=null){
        return R.success(dishDtoList);
    }
    LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
    queryWrapper.eq(Dish::getStatus,1);
    queryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
    List<Dish> list = dishService.list(queryWrapper);
    dishDtoList = list.stream().map((item)->{
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(item,dishDto);
        Long categoryId = item.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category!=null){
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
        }
        Long dishId = item.getId();
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> dishFlavorList =  dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }).collect(Collectors.toList());
    redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
    return R.success(dishDtoList);
}

}
