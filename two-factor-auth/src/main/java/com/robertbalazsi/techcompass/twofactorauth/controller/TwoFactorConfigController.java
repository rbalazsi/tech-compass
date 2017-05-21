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
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/2faconf")
public class TwoFactorConfigController {

    @Autowired
    private TwoFactorService twoFactorService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServletContext servletContext;

    @RequestMapping(method = RequestMethod.GET)
    public String twoFactorConfig(Model model) {
        Account account = userService.getCurrentAccount();
        boolean twoFactorEnabled = account.isTwoFactorEnabled();
        model.addAttribute("twoFactorEnabled", twoFactorEnabled);
        if (!account.isTwoFactorEnabled()) {
            String secret = twoFactorService.generateBase32SecretKey();
            account.setTwoFactorSecret(secret);
            userService.updateAccount(account);
            twoFactorService.generateQRCodePNG(secret, account.getEmail(), 400, 400);

            model.addAttribute("qrPath", "/files/" + secret + ".png");
            model.addAttribute("base32Secret", secret);
        }

        return "2faconf";
    }

    @RequestMapping(path = "/action", method = RequestMethod.POST)
    public String action(HttpServletRequest request, @RequestParam(name = "action") String action) {
        Account account = userService.getCurrentAccount();
        boolean twoFactorEnabled = account.isTwoFactorEnabled();

        try {
            switch (action) {
                case "ok":
                    // Confirm enabling 2FA:
                    if (!twoFactorEnabled) {
                        if (account.getTwoFactorSecret() == null) {
                            return "redirect:/login";
                        }
                        account.setTwoFactorEnabled(true);
                        userService.updateAccount(account);
                        request.logout();
                        return "redirect:/login";
                    }
                    // Confirm disabling 2FA:
                    else {
                        String secret = account.getTwoFactorSecret();
                        twoFactorService.cleanupQRCodePNG(secret);
                        account.setTwoFactorEnabled(false);
                        account.setTwoFactorSecret(null);
                        userService.updateAccount(account);
                        return "redirect:/home";
                    }
                case "cancel":
                    String secret = account.getTwoFactorSecret();
                    // Cancel enabling 2FA
                    if (!account.isTwoFactorEnabled() && secret != null) {
                        twoFactorService.cleanupQRCodePNG(secret);
                        account.setTwoFactorSecret(null);
                        userService.updateAccount(account);
                    }
                    return "redirect:/home";
                default:
                    throw new IllegalStateException("Invalid action: " + action);
            }

        } catch (ServletException e) {
            throw new IllegalStateException(e);
        }
    }
}
