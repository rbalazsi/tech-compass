package com.robertbalazsi.techcompass.twofactorauth.controller;

import com.robertbalazsi.techcompass.twofactorauth.account.Account;
import com.robertbalazsi.techcompass.twofactorauth.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping("/hello")
    public String hello(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Account account = accountRepository.findByEmail(email);

        model.addAttribute("twoFactorEnabled", account.isTwoFactorEnabled());

        return "hello";
    }
}
