package com.nttdata.bootcamp.mscredits.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class Client.
 * Credit microservice class Client.
 */
@Document(collection = "Credit")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Credit {

    @Id
    private String idCredit;

    private Client client;

    @NotNull(message = "no debe estar nulo")
    private Integer creditNumber;

    @NotEmpty(message = "no debe estar vacío")
    private String creditType;

    @NotEmpty(message = "no debe estar vacío")
    private Double creditLineAmount;

    @NotEmpty(message = "no debe estar vacío")
    private String currency;

    private Boolean status;

    private Double balance;

    private LocalDateTime disbursementDate;

    private LocalDateTime paymentDate;

    private LocalDateTime expirationDate;

}
