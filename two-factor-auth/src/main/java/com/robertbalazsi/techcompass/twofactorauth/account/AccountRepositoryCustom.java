package com.robertbalazsi.techcompass.twofactorauth.account;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AccountRepositoryCustom {

    Account save(Account account);
}
