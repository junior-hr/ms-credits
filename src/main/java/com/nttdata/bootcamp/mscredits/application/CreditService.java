package com.nttdata.bootcamp.mscredits.application;

import com.nttdata.bootcamp.mscredits.dto.CreditDto;
import com.nttdata.bootcamp.mscredits.model.Credit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class CreditService.
 * Credit microservice class CreditService.
 */
public interface CreditService {

    public Flux<Credit> findAll();

    public Mono<Credit> findById(String idCredit);

    public Mono<Credit> save(CreditDto creditDto);

    public Mono<Credit> update(CreditDto creditDto, String idCredit);

    public Mono<Void> delete(String idCredit);

    public Flux<Credit> findByDocumentNumber(String documentNumber);

    public Mono<Credit> findByCreditNumber(Integer creditNumber);

    public Mono<CreditDto> findMovementsByDocumentNumber(String documentNumber);

    public Flux<Credit> findCreditByDocumentNumber(String documentNumber);
}
