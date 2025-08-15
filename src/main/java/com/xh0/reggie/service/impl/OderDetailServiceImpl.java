package com.xh0.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xh0.reggie.entity.OrderDetail;
import com.xh0.reggie.mapper.OrderDetailMapper;
import com.xh0.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
