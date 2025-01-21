package com.teragrep.cfe18;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
@RequestMapping(path="/version")
@SecurityRequirement(name="api")
public class ApiSessionController {

    @Autowired
    DataSource dataSource;

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    ApiSessionMapper apiSessionMapper;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public int version(){
        return apiSessionMapper.getSession();
    }

}
