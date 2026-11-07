package org.ngueoko.bankaccountservice.entities;

import org.ngueoko.bankaccountservice.enums.AccountType;
import org.springframework.data.rest.core.config.Projection;

import java.util.UUID;

@Projection(types= BankAccount.class,name="p1")
public interface AccountProjection {
    UUID getId();
    AccountType getType();
}
