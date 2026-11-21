package com.uniwork.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FinanceViewController {

    @GetMapping("/employer/finance")
    public String employerFinance() {
        return "employer/finance";
    }

    @GetMapping("/admin/finance")
    public String adminFinance() {
        return "admin/finance";
    }
}
