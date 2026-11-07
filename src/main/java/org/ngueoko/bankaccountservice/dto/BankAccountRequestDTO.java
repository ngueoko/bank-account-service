package org.ngueoko.bankaccountservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngueoko.bankaccountservice.enums.AccountStatus;
import org.ngueoko.bankaccountservice.enums.AccountType;

@NoArgsConstructor @AllArgsConstructor @Builder
@Data
public class BankAccountRequestDTO {
    private Double balance;
    private AccountType type;
    private AccountStatus status;
    private String currency;
}
