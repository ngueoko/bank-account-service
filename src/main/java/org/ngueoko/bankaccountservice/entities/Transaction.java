package org.ngueoko.bankaccountservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;
import org.ngueoko.bankaccountservice.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bank_transaction") // "transaction" est un mot réservé en SQL
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "bankAccount") // évite la boucle infinie avec BankAccount.toString()
public class Transaction {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // DEBIT ou CREDIT

    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @PrePersist
    void onCreate() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }
}
