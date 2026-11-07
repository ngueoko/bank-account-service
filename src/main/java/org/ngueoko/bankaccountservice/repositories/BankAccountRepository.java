package org.ngueoko.bankaccountservice.repositories;

import org.ngueoko.bankaccountservice.entities.BankAccount;
import org.ngueoko.bankaccountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;


import java.util.List;
import java.util.UUID;

@RepositoryRestResource
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
   @RestResource(path="/bytype")
    List<BankAccount> findByType(@Param("t") AccountType type);
}
