package com.robertbalazsi.techcompass.twofactorauth.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom {

    Account findByEmail(String email);
}
