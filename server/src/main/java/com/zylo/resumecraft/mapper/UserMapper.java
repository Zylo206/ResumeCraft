package com.zylo.resumecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zylo.resumecraft.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

