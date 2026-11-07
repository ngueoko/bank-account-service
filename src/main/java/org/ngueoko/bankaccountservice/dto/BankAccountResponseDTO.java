package org.ngueoko.bankaccountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngueoko.bankaccountservice.enums.AccountStatus;
import org.ngueoko.bankaccountservice.enums.AccountType;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BankAccountResponseDTO {
    private UUID id;
    private Double balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AccountType type;
    private AccountStatus status;
    private String currency;
}
