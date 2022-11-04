package com.nttdata.bootcamp.mscredits.model;

import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class Loan.
 * Credit microservice class Loan.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Loan {

    @Id
    private String idLoan;
    private Client client;
    private Integer loanNumber;
    private String loanType;
    private Double loanAmount;
    private String currency;
    private Integer numberQuotas;
    private String status;
    //private Double balance;
    private Double debtBalance;
    private LocalDateTime disbursementDate;
    private LocalDateTime paymentDate;
    private LocalDateTime expirationDate;

}
