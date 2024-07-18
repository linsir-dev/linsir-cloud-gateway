package com.linsir.gateway.controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author yuxiaolin
 * @title: IndexController
 * @projectName linsir
 * @description: TODO
 * @date 2022/2/21 7:06 下午
 */
@RestController
@RequestMapping("/")
public class IndexController {

    @RequestMapping("")
    public String index()
    {
        return "xxxxasasasasa";
    }


    @GetMapping("cms")
    public String cms()
    {
        return "cmcGet";
    }


    /**
     * 第二版本调试使用
     * @return
     */
    @PostMapping("login")
    public String login(@RequestBody LoginParams loginParams)
    {
        return "xxxxxx";
    }
}
