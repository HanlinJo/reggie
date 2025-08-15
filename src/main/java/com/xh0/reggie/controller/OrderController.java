package com.xh0.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xh0.reggie.common.R;
import com.xh0.reggie.entity.Category;
import com.xh0.reggie.entity.Orders;
import com.xh0.reggie.mapper.OrderDetailMapper;
import com.xh0.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
    orderService.submit(orders);
     return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        Page<Orders> pageInfo = new Page(page,pageSize);

        //条件构造器(排序)
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Orders::getOrderTime);
        //执行查询
        orderService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }
}
