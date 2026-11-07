package org.ngueoko.bankaccountservice.entities;



import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.ngueoko.bankaccountservice.enums.AccountStatus;
import org.ngueoko.bankaccountservice.enums.AccountType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Builder
@Data
@Table(name ="bank_account")
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "transactions")
@Entity
public class BankAccount {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(length = 3)
    private String currency; // ex : "MAD", "EUR"
/*
    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
*/

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

}
