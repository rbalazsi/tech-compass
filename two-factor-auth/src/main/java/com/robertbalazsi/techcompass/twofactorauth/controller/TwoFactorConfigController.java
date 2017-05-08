package com.robertbalazsi.techcompass.twofactorauth.controller;

import com.robertbalazsi.techcompass.twofactorauth.account.Account;
import com.robertbalazsi.techcompass.twofactorauth.account.TwoFactorService;
import com.robertbalazsi.techcompass.twofactorauth.account.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;

@Controller
public class TwoFactorConfigController {

    //TODO: externalize as files.dir
    private static final String QRCODES_BASEDIR = "/tmp/files";

    @Autowired
    private TwoFactorService twoFactorService;

    @Autowired
    private UserService userService;

    @Autowired
    private ServletContext servletContext;

    @RequestMapping("/2faconf")
    public String twoFactorConfig(Model model) {
        String secret = twoFactorService.generateBase32SecretKey();
        Account account = userService.getCurrentAccount();
        account.setTwoFactorEnabled(true);
        account.setTwoFactorSecret(secret);
        userService.updateAccount(account);

//        String qrPath = servletContext.getRealPath("/" + QRCODES_BASEDIR + secret + ".png");
        String qrFile = secret + ".png";
        twoFactorService.generateQRCodePNG(secret, account.getEmail(), QRCODES_BASEDIR + "/" + qrFile, 400, 400);

        model.addAttribute("qrPath", "/files/" + qrFile);
        model.addAttribute("base32Secret", secret);

        return "2faconf";
    }
}
