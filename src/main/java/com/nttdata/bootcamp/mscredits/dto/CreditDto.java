package com.nttdata.bootcamp.mscredits.dto;

import com.nttdata.bootcamp.mscredits.exception.ResourceNotFoundException;
import com.nttdata.bootcamp.mscredits.model.Client;
import com.nttdata.bootcamp.mscredits.model.Credit;
import com.nttdata.bootcamp.mscredits.model.Movement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class CreditDto {

    private String idCredit;

    private Integer documentNumber;

    @NotNull(message = "no debe estar nulo")
    private Integer creditNumber;

    @NotEmpty(message = "no debe estar vacío")
    private String creditType;

    @NotNull(message = "no debe estar nulo")
    private Double creditLineAmount;

    @NotEmpty(message = "no debe estar vacío")
    private String currency;

    private Boolean status;

    private Double balance;

    private List<Movement> movements;

    public Mono<Boolean> validateFields() {
        log.info("validateFields-------: ");
        return Mono.when(validateCreditType())
                .then(Mono.just(true));
    }

    public Mono<Boolean> validateCreditType() {
        log.info("Inicio validateCreditType-------: ");
        return Mono.just(this.getCreditType()).flatMap(ct -> {
            Boolean isOk = false;
            if (this.getCreditType().equalsIgnoreCase("Personal")) { //Tarjeta de credito personal.
                isOk = true;
            } else if (this.getCreditType().equalsIgnoreCase("Business")) { //Tarjeta de credito Empresarial.
                isOk = true;
            } else {
                return Mono.error(new ResourceNotFoundException("Tarjeta de credito", "LoanType", this.getCreditType()));
            }
            log.info("Fin validateCreditType-------: ");
            return Mono.just(isOk);
        });
    }

    public Mono<Credit> mapperToCredit(Client client) {
        log.info("Inicio MapperToCredit-------: ");
        Credit credit = Credit.builder()
                .idCredit(this.idCredit)
                .client(client)
                .creditNumber(this.getCreditNumber())
                .creditType(this.getCreditType())
                .creditLineAmount(this.getCreditLineAmount())
                .currency(this.getCurrency())
                .status(this.getStatus())
                .balance(this.getBalance())
                .build();
        log.info("Fin MapperToCredit-------: ");
        return Mono.just(credit);
    }

}
