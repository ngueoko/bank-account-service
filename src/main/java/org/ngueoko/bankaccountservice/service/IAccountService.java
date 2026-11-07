package org.ngueoko.bankaccountservice.service;

import org.ngueoko.bankaccountservice.dto.BankAccountRequestDTO;
import org.ngueoko.bankaccountservice.dto.BankAccountResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IAccountService {
    List<BankAccountResponseDTO> bankAccounts();
    BankAccountResponseDTO bankAccount(UUID id);
    BankAccountResponseDTO addAccount(BankAccountRequestDTO account);
    BankAccountResponseDTO update(UUID id, BankAccountRequestDTO bankAccountRequestDTO);
    void delete(UUID id);
}
