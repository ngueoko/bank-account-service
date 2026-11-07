package org.ngueoko.bankaccountservice;

import org.ngueoko.bankaccountservice.entities.BankAccount;
import org.ngueoko.bankaccountservice.enums.AccountStatus;
import org.ngueoko.bankaccountservice.enums.AccountType;
import org.ngueoko.bankaccountservice.repositories.BankAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
public class BankAccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankAccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(BankAccountRepository bankAccountRepository) {
        return agrs -> {
           /*
            for (int i = 0; i < 10; i++) {
                BankAccount account = BankAccount.builder()
                       // .id(UUID.randomUUID())
                        .balance(ThreadLocalRandom.current().nextDouble(1000, 100000))
                        .type(i % 2 == 0 ? AccountType.CURRENT_ACCOUNT : AccountType.SAVING_ACCOUNT)
                        .status(AccountStatus.values()[i % AccountStatus.values().length])
                        .currency("MAD")
                        .build(); // createdAt est rempli par @PrePersist
                bankAccountRepository.save(account);
            }
            bankAccountRepository.findAll().forEach(System.out::println);

            */
        };

    }
}
