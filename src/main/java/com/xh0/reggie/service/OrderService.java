package com.xh0.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xh0.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}
