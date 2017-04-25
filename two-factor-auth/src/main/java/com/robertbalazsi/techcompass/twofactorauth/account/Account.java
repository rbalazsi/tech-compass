package com.robertbalazsi.techcompass.twofactorauth.account;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "accounts")
public class Account implements Serializable {

    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @Column(unique = true)
    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private boolean twoFactorEnabled;

    @Getter
    @Setter
    private String twoFactorSecret;

    protected Account() {
        // needed for JPA
    }

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
