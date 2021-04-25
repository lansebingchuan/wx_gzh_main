package com.zht.gzh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p> 首页控制器 </p>
 *
 * @author: ZHT
 * @create: 2021-02-04 16:28
 **/
@RestController
public class IndexController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

}
