package org.ngueoko.bankaccountservice.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.ngueoko.bankaccountservice.dto.BankAccountRequestDTO;
import org.ngueoko.bankaccountservice.dto.BankAccountResponseDTO;
import org.ngueoko.bankaccountservice.entities.BankAccount;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BankAccountMapper  {
     BankAccount sourceToDestination(BankAccountRequestDTO bankAccountRequestDTO);
     BankAccountResponseDTO destinationToSource(BankAccount bankAccount);
     List<BankAccountResponseDTO>  fromBankAccountList(List<BankAccount> bankAccounts);

     //Test de sur cette Methode
     @AfterMapping
    default void setUpdatedDate(@MappingTarget BankAccount bankAccount){
         bankAccount.setUpdatedAt(LocalDateTime.now());
     }
    //Test de sur cette Methode
    @AfterMapping
    default void setcreatedDate(@MappingTarget BankAccount bankAccount){
        bankAccount.setCreatedAt(LocalDateTime.now());
    }
}
