package com.nttdata.bootcamp.mscredits.infrastructure;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.nttdata.bootcamp.mscredits.dto.CreditDto;
import com.nttdata.bootcamp.mscredits.model.Credit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Class CreditRepository.
 * Credit microservice class CreditRepository.
 */
public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {

    @Query(value = "{'client.documentNumber' : ?0}")
    Flux<Credit> findByCreditClient(String documentNumber);

    @Query(value = "{'creditNumber' : ?0}")
    Mono<Credit> findCreditByCreditNumber(Integer creditNumber);

    @Query(value = "{'client.documentNumber' : ?0}")
    Mono<CreditDto> findByDocumentNumber(String documentNumber);
}