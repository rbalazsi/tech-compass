package com.robertbalazsi.techcompass.twofactorauth.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

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
        return new User(account.getEmail(), account.getPassword(), Collections.singleton(new SimpleGrantedAuthority(ROLE_USER)));
    }
}
