package com.uniwork.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller chuẩn theo Slide 1.1 & Slide 1.2 (Trang 22 - 27) FPT Polytechnic
 * Minh họa @Controller, @RequestMapping, Model, model.addAttribute và return view-path
 */
@Controller
public class HelloController {

    @RequestMapping("/poly/hello")
    public String sayHello(Model model) {
        model.addAttribute("title", "FPT Polytechnic");
        model.addAttribute("subject", "Spring Boot MVC");
        return "demo/hello";
    }
}
