package com.nttdata.bootcamp.mscredits.infrastructure;

import com.nttdata.bootcamp.mscredits.dto.CreditDto;
import com.nttdata.bootcamp.mscredits.model.Credit;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {

    @Query(value = "{'client.documentNumber' : ?0}")
    Flux<Credit> findByCreditClient(String documentNumber);

    @Query(value = "{'creditNumber' : ?0}")
    Mono<Credit> findByCreditNumber(String creditNumber);

    @Query(value = "{'client.documentNumber' : ?0}")
    Mono<CreditDto> findByDocumentNumber(String documentNumber);
}