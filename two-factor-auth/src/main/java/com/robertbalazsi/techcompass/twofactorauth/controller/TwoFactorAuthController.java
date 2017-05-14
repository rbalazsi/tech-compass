package com.robertbalazsi.techcompass.twofactorauth.controller;

import com.robertbalazsi.techcompass.twofactorauth.account.Account;
import com.robertbalazsi.techcompass.twofactorauth.account.TwoFactorService;
import com.robertbalazsi.techcompass.twofactorauth.account.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/validate")
public class TwoFactorAuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private TwoFactorService twoFactorService;

    @RequestMapping(method = RequestMethod.GET)
    public String show2FAForm() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth.isAuthenticated()) {
//            return "redirect:/hello";
//        }
        Account account = userService.getCurrentAccount();
        return account.isTwoFactorEnabled() ? "validate" : "redirect:/hello";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String validateCode(String code) {
        Account account = userService.getCurrentAccount();
        String generated = twoFactorService.getNextTOTP(account.getTwoFactorSecret());
        if (generated.equals(code)) {
            grantUserRole();
            return "redirect:/hello";
        } else {
            return "redirect:/validate?error";
        }
    }

    private void grantUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = new ArrayList<>(auth.getAuthorities());
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
