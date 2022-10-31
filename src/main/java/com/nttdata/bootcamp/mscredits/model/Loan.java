package com.nttdata.bootcamp.mscredits.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
    private Double balance;
}
