package com.xh0.reggie.controller;

import com.xh0.reggie.common.R;
import com.xh0.reggie.entity.Orders;
import com.xh0.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    private OrderService  orderService;

}
