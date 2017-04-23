package com.robertbalazsi.techcompass.twofactorauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TwoFactorConfigController {

    @RequestMapping("/2faconf")
    public String twoFactorConfig(Model model) {

        return "2faconf";
    }
}
