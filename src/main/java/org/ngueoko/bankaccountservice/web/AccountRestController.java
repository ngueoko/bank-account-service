package org.ngueoko.bankaccountservice.web;

import lombok.AllArgsConstructor;
import org.ngueoko.bankaccountservice.dto.BankAccountRequestDTO;
import org.ngueoko.bankaccountservice.dto.BankAccountResponseDTO;
import org.ngueoko.bankaccountservice.service.IAccountService;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class AccountRestController {

    private final IAccountService iAccountService;

    @GetMapping("/bankAccounts")
    public List<BankAccountResponseDTO> bankAccounts() {
        return iAccountService.bankAccounts();
    }

    @GetMapping("/bankAccounts/{id}")
    public  BankAccountResponseDTO bankAccount(@PathVariable UUID id) {
        return iAccountService.bankAccount(id);
    }

    @PostMapping("/bankAccounts")
    public BankAccountResponseDTO save(@RequestBody BankAccountRequestDTO bankAccountRequestDTO) {
        return iAccountService.addAccount(bankAccountRequestDTO);
    }

    @PutMapping("/bankAccounts/{id}")
    public BankAccountResponseDTO update(@PathVariable UUID id, @RequestBody  BankAccountRequestDTO bankAccount) {
        return iAccountService.update(id,bankAccount);
    }

    @DeleteMapping("/bankAccounts/{id}")
    public  void delete(@PathVariable UUID id) {
        iAccountService.delete(id);
    }

}
