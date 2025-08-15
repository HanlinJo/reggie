package com.xh0.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xh0.reggie.entity.User;
import com.xh0.reggie.service.UserService;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
