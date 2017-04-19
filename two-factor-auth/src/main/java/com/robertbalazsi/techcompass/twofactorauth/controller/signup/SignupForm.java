package com.robertbalazsi.techcompass.twofactorauth.controller.signup;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class SignupForm {

    private static final String NOT_BLANK_MSG = "Field must not be blank.";
    private static final String INVALID_EMAIL_MSG = "Invalid email address given.";

    @Getter
    @Setter
    @NotBlank(message = NOT_BLANK_MSG)
    @Email(message = INVALID_EMAIL_MSG)
    private String email;

    @Getter
    @Setter
    @NotBlank(message = NOT_BLANK_MSG)
    private String password;
}
