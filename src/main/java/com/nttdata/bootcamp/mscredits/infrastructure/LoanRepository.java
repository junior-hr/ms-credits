package com.nttdata.bootcamp.mscredits.infrastructure;

import com.nttdata.bootcamp.mscredits.config.WebClientConfig;
import com.nttdata.bootcamp.mscredits.model.Loan;
import com.nttdata.bootcamp.mscredits.util.Constants;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class LoanRepository {

    @Value("${local.property.host.ms-loan}")
    private String propertyHostMsLoans;

    @Autowired
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

	@CircuitBreaker(name = Constants.LOAN_CB, fallbackMethod = "getDefaultLoanByDocumentNumber")
    public Flux<Loan> findLoansByDocumentNumber(String documentNumber) {

        log.info("Inicio----findLoansByDocumentNumber-------: ");
        WebClientConfig webconfig = new WebClientConfig();
        Flux<Loan> alerts = webconfig.setUriData("http://" + propertyHostMsLoans + ":8081")
                .flatMap(d -> webconfig.getWebclient().get()
                        .uri("/api/loans/loansDetails/" + documentNumber).retrieve()
                        .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new Exception("Error 400")))
                        .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new Exception("Error 500")))
                        .bodyToFlux(Loan.class)
                        // .transform(it -> reactiveCircuitBreakerFactory.create("parameter-service").run(it, throwable -> Flux.just(new Loan())))
                        .collectList()
                )
                .flatMapMany(iterable -> Flux.fromIterable(iterable));
        return alerts;
    }
    
    public Flux<Loan> getDefaultLoanByDocumentNumber(String documentNumber, Exception e) {
	    return Flux.empty();
    }
}
