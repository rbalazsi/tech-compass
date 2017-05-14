package com.robertbalazsi.techcompass.twofactorauth.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private static final String ROLE_PRE_AUTH_USER = "ROLE_PRE_AUTH_USER";
    private static final String ROLE_USER = "ROLE_USER";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("User not found.");
        }

        return createUser(account);
    }

    public Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return accountRepository.findByEmail(email);
    }

    public void updateAccount(Account account) {
        accountRepository.save(account);
    }

    public void signin(Account account) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public Account signup(String email, String password) {
        String encodedPass = passwordEncoder.encode(password);
        Account account = new Account(email, encodedPass);
        return accountRepository.save(account);
    }

    private User createUser(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_PRE_AUTH_USER));
        if (!account.isTwoFactorEnabled()) {
            authorities.add(new SimpleGrantedAuthority(ROLE_USER));
        }
        return new User(account.getEmail(), account.getPassword(), authorities);
    }
}
