package com.zylo.resumecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zylo.resumecraft.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {
}

