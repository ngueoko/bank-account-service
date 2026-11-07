package org.ngueoko.bankaccountservice.service;

import lombok.AllArgsConstructor;
import org.ngueoko.bankaccountservice.dto.BankAccountRequestDTO;
import org.ngueoko.bankaccountservice.dto.BankAccountResponseDTO;
import org.ngueoko.bankaccountservice.entities.BankAccount;
import org.ngueoko.bankaccountservice.mappers.BankAccountMapper;
import org.ngueoko.bankaccountservice.repositories.BankAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Transactional
@AllArgsConstructor
@Service
public class AccountServiceImpl implements IAccountService{
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountsMapper;


    @Override
    public List<BankAccountResponseDTO> bankAccounts() {
        return bankAccountsMapper.fromBankAccountList(bankAccountRepository.findAll());
    }

    @Override
    public BankAccountResponseDTO bankAccount(UUID id) {
       BankAccount bankAccount = bankAccountRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Bank account with id " + id + " not found")
        );
        return bankAccountsMapper.destinationToSource(bankAccount);
    }

    @Override
    public BankAccountResponseDTO addAccount(BankAccountRequestDTO accountRequestDTO){
        return  bankAccountsMapper.destinationToSource(bankAccountRepository.save(
                bankAccountsMapper.sourceToDestination(accountRequestDTO)));
    }

    @Override
    public BankAccountResponseDTO update(UUID id, BankAccountRequestDTO bankAccountRequestDTO) {
        BankAccount account = bankAccountRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Bank account with id " + id + " not found"));
        BankAccount accountToSave = bankAccountsMapper.sourceToDestination(bankAccountRequestDTO);
        accountToSave.setId(account.getId());
        accountToSave.setCreatedAt(account.getCreatedAt());
        return bankAccountsMapper.destinationToSource(bankAccountRepository.save(accountToSave));
    }

    @Override
    public void delete(UUID id) {
        bankAccountRepository.deleteById(id);
    }
}
