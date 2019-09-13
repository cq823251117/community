package com.example.community.controller;

import com.example.community.dto.PaginationDTO;
import com.example.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    
    

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    //page和size参数的目的是因页数不同限制返回数据的位置和个数
    public String index(
                        Model model,
                        @RequestParam(value = "page",defaultValue = "1") Integer page,
                        @RequestParam(value = "size",defaultValue = "5") Integer size){


        //从数据库中查询出各个用户提交的发布信息
        PaginationDTO pagination=questionService.list(page,size);
        model.addAttribute("pagination",pagination);
        return "index";
    }
}
