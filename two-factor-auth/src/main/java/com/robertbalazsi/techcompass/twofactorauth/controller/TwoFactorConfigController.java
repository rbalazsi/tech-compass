package com.robertbalazsi.techcompass.twofactorauth.controller;

import com.robertbalazsi.techcompass.twofactorauth.account.Account;
import com.robertbalazsi.techcompass.twofactorauth.account.TwoFactorService;
import com.robertbalazsi.techcompass.twofactorauth.account.UserService;
import com.robertbalazsi.techcompass.twofactorauth.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/2faconf")
public class TwoFactorConfigController {

    @Autowired
    private ApplicationProperties appProps;

    @Autowired
    private TwoFactorService twoFactorService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServletContext servletContext;

    @RequestMapping(method = RequestMethod.GET)
    public String twoFactorConfig(Model model) {
        Account account = userService.getCurrentAccount();
        String secret;
        if (account.isTwoFactorEnabled()) {
            secret = account.getTwoFactorSecret();
        } else {
            secret = twoFactorService.generateBase32SecretKey();
            account.setTwoFactorEnabled(true);
            account.setTwoFactorSecret(secret);
            userService.updateAccount(account);

            twoFactorService.generateQRCodePNG(secret, account.getEmail(), appProps.getFilesDir() + "/" + secret + ".png", 400, 400);
        }

        model.addAttribute("qrPath", "/files/" + secret + ".png");
        model.addAttribute("base32Secret", secret);

        return "2faconf";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String logout(HttpServletRequest request) {
        try {
            request.logout();
            return "redirect:/login";
        } catch (ServletException e) {
            throw new IllegalStateException(e);
        }
    }
}
