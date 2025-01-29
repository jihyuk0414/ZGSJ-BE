package com.example.Attendance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "store_employee",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"store_id", "email"}
                )
        }
)
@Getter
@NoArgsConstructor
public class StoreEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "se_id")
    private Integer id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "salary", nullable = false)
    private Long salary;

    @Column(name = "employment_type", nullable = false)
    @Min(0)
    private Byte employmentType;

    @Column(name = "bank_code", length = 50, nullable = false)
    private String bankCode;

    @Column(name = "account_number", length = 50, nullable = false)
    private String accountNumber;

    @Column(name = "payment_date", nullable = false)
    @Min(1)
//    @Max(28)
    private Integer paymentDate;


    @Column(name = "birth_date",nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone_number",nullable = false,length = 50)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
}
