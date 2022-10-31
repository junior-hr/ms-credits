package com.nttdata.bootcamp.mscredits.application;

import com.nttdata.bootcamp.mscredits.dto.CreditDto;
import com.nttdata.bootcamp.mscredits.infrastructure.ClientRepository;
import com.nttdata.bootcamp.mscredits.infrastructure.LoanRepository;
import com.nttdata.bootcamp.mscredits.infrastructure.MovementRepository;
import com.nttdata.bootcamp.mscredits.model.Client;
import com.nttdata.bootcamp.mscredits.model.Movement;
import com.nttdata.bootcamp.mscredits.util.Constants;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;
import com.nttdata.bootcamp.mscredits.model.Credit;
import org.springframework.beans.factory.annotation.Autowired;
import com.nttdata.bootcamp.mscredits.infrastructure.CreditRepository;
import com.nttdata.bootcamp.mscredits.exception.ResourceNotFoundException;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Override
    public Flux<Credit> findAll() {
        return creditRepository.findAll();
    }

    @Override
    public Mono<Credit> findById(String idCredit) {
        return Mono.just(idCredit)
                .flatMap(creditRepository::findById)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Credito", "IdCredito", idCredit)));
    }

    @Override
    public Mono<Credit> save(CreditDto creditDto) {
        return clientRepository.findClientByDni(String.valueOf(creditDto.getDocumentNumber()))
                .flatMap(client -> {
                    return this.validateCreditType(creditDto, client)
                            .flatMap(at -> {
                                if (at.equals(true)) {
                                    return creditDto.mapperToCredit(client)
                                            .flatMap(ba -> {
                                                log.info("sg MapperToCredit-------: ");
                                                return creditRepository.save(ba);
                                            });
                                } else {
                                    return Mono.error(new ResourceNotFoundException("Tarjeta de credito", "CreditType", creditDto.getCreditType()));
                                }

                            });
                });
    }

    @Override
    public Mono<Credit> update(CreditDto creditDto, String idCredit) {

        return clientRepository.findClientByDni(String.valueOf(creditDto.getDocumentNumber()))
                .flatMap(client -> {
                    return creditDto.validateFields()
                            .flatMap(at -> {
                                if (at.equals(true)) {
                                    return creditRepository.findById(idCredit)
                                            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Credit", "IdCredito", idCredit)))
                                            .flatMap(c -> {
                                                c.setClient(client);
                                                c.setCreditNumber(creditDto.getCreditNumber() == null ? c.getCreditNumber() : creditDto.getCreditNumber());
                                                c.setCreditType(creditDto.getCreditType() == null ? c.getCreditType() : creditDto.getCreditType());
                                                c.setCreditLineAmount(creditDto.getCreditLineAmount() == null ? c.getCreditLineAmount() : creditDto.getCreditLineAmount());
                                                c.setCurrency(creditDto.getCurrency() == null ? c.getCurrency() : creditDto.getCurrency());
                                                c.setStatus(creditDto.getStatus() == null ? c.getStatus() : creditDto.getStatus());
                                                c.setBalance(creditDto.getBalance() == null ? c.getBalance() : creditDto.getBalance());
                                                return creditRepository.save(c);

                                            });
                                } else {
                                    return Mono.error(new ResourceNotFoundException("Tarjeta de Credito", "CreditType", creditDto.getCreditType()));
                                }
                            });
                });
    }

    @Override
    public Mono<Void> delete(String idCredit) {
        return creditRepository.findById(idCredit)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Credito", "IdCredito", idCredit)))
                .flatMap(creditRepository::delete);
    }

    @Override
    public Flux<Credit> findByDocumentNumber(String documentNumber) {

        log.info("Inicio----findByDocumentNumber-------: ");
        log.info("Inicio----findByDocumentNumber-------documentNumber : " + documentNumber);
        return creditRepository.findByCreditClient(documentNumber)
                .flatMap(credit -> {
                    log.info("Inicio----findByCreditClient-------: ");
                    return movementRepository.findLastMovementByCreditNumber(credit.getCreditNumber())
                            .switchIfEmpty(Mono.defer(() -> {
                                log.info("----2 switchIfEmpty-------: ");
                                Movement mv = Movement.builder()
                                        .balance(credit.getCreditLineAmount())
                                        .build();
                                return Mono.just(mv);
                            }))
                            .flatMap(m -> {
                                log.info("----findByDocumentNumber setBalance-------: ");
                                credit.setBalance(m.getBalance());
                                return Mono.just(credit);
                            });
                });
    }

    @Override
    public Mono<Credit> findByCreditNumber(String creditNumber) {
        return Mono.just(creditNumber)
                .flatMap(creditRepository::findByCreditNumber)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Credito", "creditNumber", creditNumber)));
    }

    @Override
    public Mono<CreditDto> findMovementsByDocumentNumber(String documentNumber) {
        log.info("Inicio----findMovementsByDocumentNumber-------: ");
        log.info("Inicio----findMovementsByDocumentNumber-------documentNumber : " + documentNumber);
        return creditRepository.findByDocumentNumber(documentNumber)
                .flatMap(d -> {
                    log.info("Inicio----findMovementsByCreditNumber-------: ");
                    return movementRepository.findMovementsByCreditNumber(d.getCreditNumber().toString())
                            .collectList()
                            .flatMap(m -> {
                                log.info("----findMovementsByCreditNumber setMovements-------: ");
                                d.setMovements(m);
                                return Mono.just(d);
                            });
                });
    }

    @Override
    public Flux<Credit> findCreditByDocumentNumber(String documentNumber) {
        log.info("Inicio----findCreditByDocumentNumber-------: ");
        log.info("Inicio----findCreditByDocumentNumber-------documentNumber : " + documentNumber);
        return creditRepository.findByCreditClient(documentNumber);
    }

    //Validar CreditType
    public Mono<Boolean> validateCreditType(CreditDto creditDto, Client client) {
        log.info("Inicio validateCreditType-------: ");
        return Mono.just(creditDto.getCreditType()).flatMap(ct -> {
            //Boolean isOk = false;
            if (creditDto.getCreditType().equalsIgnoreCase("Personal")) { //Tarjeta de credito personal.
                if (validateCreditDebt(client.getDocumentNumber(), "Personal").equals(true)) {
                    return validateLoanDebt(client.getDocumentNumber(), "Personal");
                } else {
                    return Mono.just(false);
                }
            } else if (creditDto.getCreditType().equalsIgnoreCase("Business")) { //Tarjeta de credito Empresarial.
                if (validateCreditDebt(client.getDocumentNumber(), "Business").equals(true)) {
                    return validateLoanDebt(client.getDocumentNumber(), "Business");
                } else {
                    return Mono.just(false);
                }
            } else {
                return Mono.error(new ResourceNotFoundException("Tarjeta de credito", "CreditType", creditDto.getCreditType()));
            }
            //log.info("Fin validateCreditType-------: ");
            //return Mono.just(isOk);
        });
    }

    //Si tiene deuda retorna false
    public Mono<Boolean> validateCreditDebt(String documentNumber, String creditType) {

        log.info("Inicio----validateLoanDebt-------: ");
        log.info("Inicio----validateLoanDebt-------documentNumber : " + documentNumber);
        LocalDateTime datetime = LocalDateTime.now();
        return creditRepository.findByCreditClient(documentNumber)
                .collectList()
                .flatMap(l -> {
                    if (creditType.equals("Personal")) {
                        if (l.size() == Constants.ZERO || l == null) {
                            return Mono.just(true);
                        } else {
                            if (datetime.isBefore(l.get(0).getExpirationDate())) {
                                return Mono.just(true);//No se vence
                            } else {
                                return Mono.just(false);//Ya se vencio
                            }
                        }
                    } else if (creditType.equals("Business")) {
                        if (l == null) {
                            return Mono.just(true);
                        } else {
                            if (datetime.isBefore(l.get(0).getExpirationDate())) {
                                return Mono.just(true);//No se vence
                            } else {
                                return Mono.just(false);//Ya se vencio
                            }
                        }
                    }
                    return Mono.just(true);
                });
    }

    //Si tiene deuda return false sino true
    public Mono<Boolean> validateLoanDebt(String documentNumber, String loanType) {

        log.info("Inicio----validateLoanDebt-------: ");
        log.info("Inicio----validateLoanDebt-------documentNumber : " + documentNumber);
        LocalDateTime datetime = LocalDateTime.now();
        return loanRepository.findLoansByDocumentNumber(documentNumber)
                .collectList()
                .flatMap(c -> {
                    if (loanType.equals("Personal")) {
                        if (c.size() == Constants.ZERO || c == null) {
                            return Mono.just(true);
                        } else {
                            if (datetime.isBefore(c.get(0).getExpirationDate())) {
                                return Mono.just(true);//No se vence
                            } else {
                                return Mono.just(false);//Ya se vencio
                            }
                        }
                    } else if (loanType.equals("Business")) {
                        if (c == null) {
                            return Mono.just(true);
                        } else {
                            if (datetime.isBefore(c.get(0).getExpirationDate())) {
                                return Mono.just(true);//No se vence
                            } else {
                                return Mono.just(false);//Ya se vencio
                            }
                        }
                    }
                    return Mono.just(true);
                });
    }

}
