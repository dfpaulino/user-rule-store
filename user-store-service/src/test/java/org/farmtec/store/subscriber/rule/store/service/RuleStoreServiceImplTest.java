package org.farmtec.store.subscriber.rule.store.service;

import org.farmtec.store.subscriber.rule.store.exceptions.RuleServiceException;
import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.farmtec.store.subscriber.rule.store.repository.UserRuleRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Flux.*;


/**
 * Created by dp on 05/08/2021
 */
class RuleStoreServiceImplTest {

    @Mock
    UserRuleRepo repo;

    private RuleStoreService ruleStoreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ruleStoreService = new RuleStoreServiceImpl(repo);


    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void saveRule() {

        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.HOUR, 10);
        RuleDocument ruleDocument1 = new RuleDocument()
                .setId("1234567890")
                .setRuleName("A")
                .setUser("Dan")
                .setRuleId("1")
                .setCreatedAt(new Date())
                .setExpireAt(exp.getTime());

        Mockito.when(repo.saveDocument(any(RuleDocument.class))).thenReturn(Mono.just(ruleDocument1));
        //when
        Mono<RuleDocument> savedMono = ruleStoreService.saveRule(ruleDocument1);

        StepVerifier.create(savedMono)
                .assertNext((r) -> {
                    assertThat(r.getId()).isEqualTo(ruleDocument1.getId());
                })
                .verifyComplete();
    }

    @Test
    void saveRule_whenDuplicated_shouldReturnRuleServiceException() {

        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.HOUR, 10);
        RuleDocument ruleDocument1 = new RuleDocument()
                .setId("1234567890")
                .setRuleName("A")
                .setUser("Dan")
                .setRuleId("1")
                .setCreatedAt(new Date())
                .setExpireAt(exp.getTime());


        Mockito.when(repo.saveDocument(any(RuleDocument.class))).thenReturn(
                Mono.error(new DuplicateKeyException("'E11000 duplicate key error collection: rules.ruleDocument index: " +
                        "user_1_ruleId_1 dup key: { user: \"user\", ruleId: \"1\" }'")));
        //When
        Mono<RuleDocument> ruleDocumentMono = ruleStoreService.saveRule(ruleDocument1);
        //then
        StepVerifier.create(ruleDocumentMono)
                .expectError(RuleServiceException.class).verify();
    }

    @Test
    void findRule() {
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.HOUR, 10);
        RuleDocument ruleDocument1 = new RuleDocument()
                .setId("1234567890")
                .setRuleName("A")
                .setUser("Dan")
                .setRuleId("1")
                .setCreatedAt(new Date())
                .setExpireAt(exp.getTime());
        RuleDocument ruleDocument2 = new RuleDocument()
                .setRuleId("1234567891")
                .setRuleName("B")
                .setUser("Dan")
                .setRuleId("2")
                .setCreatedAt(new Date())
                .setExpireAt(exp.getTime());

        //given(repo.findDocument(any(RuleDocument.class))).willReturn(Flux.just(ruleDocument1,ruleDocument2));
        Mockito.when(repo.findDocument(any(RuleDocument.class))).thenReturn(just(ruleDocument1, ruleDocument2));

        Flux<RuleDocument> ruleDocumentFlux = ruleStoreService.findRule(new RuleDocument().setUser("Dan"));

        StepVerifier.create(ruleDocumentFlux)
                .assertNext((r) -> {
                    System.out.println(r.toString());
                    assertThat(r.getUser()).isEqualTo("Dan");
                })
                .assertNext((r) -> {
                    System.out.println(r.toString());
                    assertThat(r.getUser()).isEqualTo("Dan");
                })
                .expectComplete()
                .verify();

    }

    @Test
    void findRule_whenNotFound_shouldReturnRuleServiceException() {

        given(repo.findDocument(any(RuleDocument.class))).willReturn(empty());
        //mock(UserRuleRepo.class).findDocument(any(RuleDocument.class)).thenMany(Flux.just(ruleDocument1,ruleDocument2));
        Flux<RuleDocument> ruleDocumentFlux = ruleStoreService.findRule(new RuleDocument().setUser("Dan"));
        StepVerifier.create(ruleDocumentFlux)
                .expectError(RuleServiceException.class).verify();
    }

    @Test
    void findRule_whenDataAccessException_shouldReturnRuleServiceException() {

        given(repo.findDocument(any(RuleDocument.class))).willReturn(Flux.error(new DuplicateKeyException("Duplicated Key")));
        //mock(UserRuleRepo.class).findDocument(any(RuleDocument.class)).thenMany(Flux.just(ruleDocument1,ruleDocument2));
        Flux<RuleDocument> ruleDocumentFlux = ruleStoreService.findRule(new RuleDocument().setUser("Dan"));
        StepVerifier.create(ruleDocumentFlux)
                .expectError(RuleServiceException.class).verify();
    }

    @Test
    void updateRule() {
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.HOUR, 10);
        RuleDocument ruleDocument1 = new RuleDocument()
                .setId("1234567890")
                .setRuleName("A")
                .setUser("Dan")
                .setRuleId("1")
                .setCreatedAt(new Date())
                .setExpireAt(exp.getTime());
        Mockito.when(repo.updateDocument(any(RuleDocument.class))).thenReturn(Mono.just(ruleDocument1));
        //when
        Mono<RuleDocument> updated = ruleStoreService.updateRule(ruleDocument1);

        //then
        StepVerifier.create(updated)
                .assertNext((r) -> assertThat(r.getId()).isEqualTo(ruleDocument1.getId()))
                .verifyComplete();
    }

    @Test
    void updateRule_whenDataNotFound_shouldReturnRuleServiceException() {

        //when
        Mockito.when(repo.updateDocument(any(RuleDocument.class))).thenReturn(Mono.empty());
        Mono<RuleDocument> updated = ruleStoreService.updateRule(new RuleDocument());

        StepVerifier.create(updated)
                .expectError(RuleServiceException.class)
                .verify();
    }

    @Test
    void updateRule_whenDataAccessException_shouldReturnRuleServiceException() {

        //when
        Mockito.when(repo.updateDocument(any(RuleDocument.class)))
                .thenReturn(Mono.error(new DataIntegrityViolationException("some error")));
        Mono<RuleDocument> updated = ruleStoreService.updateRule(new RuleDocument());

        StepVerifier.create(updated)
                .expectError(RuleServiceException.class)
                .verify();
    }

    @Test
    void deleteRule() {
    }
}