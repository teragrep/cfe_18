package com.teragrep.cfe18;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApiSessionMapper {

    int getSession();
}

