package com.robertbalazsi.techcompass.twofactorauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecuredController {

    @RequestMapping("/secured")
    public String secured(@RequestParam(value = "name", required = false, defaultValue = "User") String name, Model model) {
        model.addAttribute("name", name);
        return "secured";
    }
}
