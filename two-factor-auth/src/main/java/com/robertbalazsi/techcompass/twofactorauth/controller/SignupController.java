package com.robertbalazsi.techcompass.twofactorauth.controller;

import com.robertbalazsi.techcompass.twofactorauth.account.Account;
import com.robertbalazsi.techcompass.twofactorauth.account.AccountRepository;
import com.robertbalazsi.techcompass.twofactorauth.account.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;

    @RequestMapping("/signup")
    public String signup(Model model) {
        model.addAttribute(new SignupForm());
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String doSignup(@Valid @ModelAttribute SignupForm form, Errors errors) {
        if (errors.hasErrors()) {
            return "signup";
        }

        Account newAccount = userService.signup(form.getEmail(), form.getPassword());
        userService.signin(newAccount);

        return "redirect:/hello";
    }
}
