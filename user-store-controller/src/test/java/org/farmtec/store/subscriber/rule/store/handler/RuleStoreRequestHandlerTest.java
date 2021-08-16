package org.farmtec.store.subscriber.rule.store.handler;

import com.fasterxml.jackson.databind.JsonNode;
import org.farmtec.store.subscriber.rule.store.controller.config.GlobalErrorAttributes;
import org.farmtec.store.subscriber.rule.store.controller.config.GlobalErrorWebExceptionHandler;
import org.farmtec.store.subscriber.rule.store.controller.config.RouterConfig;
import org.farmtec.store.subscriber.rule.store.dto.RuleDocumentDto;
import org.farmtec.store.subscriber.rule.store.exceptions.ErrorCode;
import org.farmtec.store.subscriber.rule.store.exceptions.RuleServiceException;
import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.farmtec.store.subscriber.rule.store.model.RuleState;
import org.farmtec.store.subscriber.rule.store.service.RuleStoreService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @Author Daniel Paulino 16/08/2021
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RouterConfig.class, GlobalErrorWebExceptionHandler.class, GlobalErrorAttributes.class})
@WebFluxTest
class RuleStoreRequestHandlerTest {

    public static RuleDocument ruleDocumentSample;

    @Autowired
    private ApplicationContext ac;

    @MockBean
    private RuleStoreService ruleStoreService;

    private WebTestClient webTestClient;


    @BeforeAll
    public static void setUpAll() {
        ruleDocumentSample = new RuleDocument();
        ruleDocumentSample.setState(RuleState.CREATED);
        ruleDocumentSample.setId("1234567890");
        ruleDocumentSample.setRuleId("1");
        ruleDocumentSample.setRuleName("rule_1");
        ruleDocumentSample.setUser("user");
        ruleDocumentSample.setExpireAt(new Date());
    }

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(ac).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void save() {
        //Given
        RuleDocumentDto dto = new RuleDocumentDto();
        dto.setRuleId("1");
        dto.setRuleName("rule_1");
        dto.setUser("user");
        dto.setExpireAt(new Date());

        when(ruleStoreService.saveRule(any(RuleDocument.class))).thenReturn(Mono.just(ruleDocumentSample));
        //when
        webTestClient.post()
                .uri("/rule")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), RuleDocumentDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(RuleDocumentDto.class)
                .value(doc -> {
                    System.out.println(doc.toString());
                    assertAll(
                            () -> assertThat(doc.getId()).isEqualTo(ruleDocumentSample.getId()),
                            () -> assertThat(doc.getRuleId()).isEqualTo(ruleDocumentSample.getRuleId()),
                            () -> assertThat(doc.getRuleName()).isEqualTo(ruleDocumentSample.getRuleName()),
                            () -> assertThat(doc.getUser()).isEqualTo(ruleDocumentSample.getUser()),
                            () -> assertThat(doc.getState()).isEqualTo(ruleDocumentSample.getState().name()),
                            () -> assertThat(doc.getExpireAt()).isInstanceOf(Date.class)
                    );
                });
        //then
    }

    @Test
    void find_whenReturns1Document() {
        //Given
        when(ruleStoreService.findRule(any(RuleDocument.class))).thenReturn(Flux.just().just(ruleDocumentSample));
        //when
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rules")
                        .queryParam("user", "user")
                        .queryParam("ruleName", "rule_1")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(RuleDocumentDto.class)
                .hasSize(1)
                .consumeWith(rsp -> {
                            rsp.getResponseBody().stream().forEach(doc -> {
                                assertAll(
                                        () -> assertThat(doc.getId()).isEqualTo(ruleDocumentSample.getId()),
                                        () -> assertThat(doc.getRuleId()).isEqualTo(ruleDocumentSample.getRuleId()),
                                        () -> assertThat(doc.getRuleName()).isEqualTo(ruleDocumentSample.getRuleName()),
                                        () -> assertThat(doc.getUser()).isEqualTo(ruleDocumentSample.getUser()),
                                        () -> assertThat(doc.getState()).isEqualTo(ruleDocumentSample.getState().name()),
                                        () -> assertThat(doc.getExpireAt()).isInstanceOf(Date.class)
                                );
                            });
                        }
                );
        //then
    }

    @Test
    void find_whenReturns2Document() {
        //Given
        when(ruleStoreService.findRule(any(RuleDocument.class))).thenReturn(Flux.just().just(ruleDocumentSample,ruleDocumentSample));
        //when
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rules")
                        .queryParam("user", "user")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(RuleDocumentDto.class)
                .hasSize(2)
                .consumeWith(rsp -> {
                            rsp.getResponseBody().stream().forEach(doc -> {
                                System.out.println(doc.toString());
                                assertAll(
                                        () -> assertThat(doc.getId()).isEqualTo(ruleDocumentSample.getId()),
                                        () -> assertThat(doc.getRuleId()).isEqualTo(ruleDocumentSample.getRuleId()),
                                        () -> assertThat(doc.getRuleName()).isEqualTo(ruleDocumentSample.getRuleName()),
                                        () -> assertThat(doc.getUser()).isEqualTo(ruleDocumentSample.getUser()),
                                        () -> assertThat(doc.getState()).isEqualTo(ruleDocumentSample.getState().name()),
                                        () -> assertThat(doc.getExpireAt()).isInstanceOf(Date.class)
                                );
                            });
                        }
                );
        //then
    }

    @Test
    void find_whenNotFound() {
        //Given
        when(ruleStoreService.findRule(any(RuleDocument.class))).thenReturn(Flux.error(() -> {
            return new RuleServiceException("Document not Found", ErrorCode.DOCUMENT_NOT_FOUND);
        }));
        //when
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rules")
                        .queryParam("user", "user")
                        .queryParam("ruleName", "rule_1")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(JsonNode.class)
                .value(s -> {System.out.println(s);
                            assertThat(s.get("status").asText()).isEqualTo("NOT_FOUND");
                            assertThat(s.get("message").asText()).isEqualTo("Document not Found");
                            assertThat(s.get("exception").asText()).isEqualTo("org.farmtec.store.subscriber.rule.store.exceptions.RuleServiceException");

                });
        //then
    }

    @Test
    void find_whenDataAccessException() {
        //Given
        when(ruleStoreService.findRule(any(RuleDocument.class))).thenReturn(Flux.error(() -> {
            return new RuleServiceException(new DataAccessResourceFailureException("cant connect to mongo"));
        }));
        //when
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rules")
                        .queryParam("user", "user")
                        .queryParam("ruleName", "rule_1")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value())
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(JsonNode.class)
                .value(s -> {System.out.println(s);
                    assertThat(s.get("status").asText()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.name());
                    assertThat(s.get("message").asText()).isEqualTo("Storage Service Unavailable");
                    assertThat(s.get("exception").asText()).isEqualTo("org.farmtec.store.subscriber.rule.store.exceptions.RuleServiceException");

                });
        //then
    }


    @Test
    void update() {
        //Given
        RuleDocumentDto dto = new RuleDocumentDto();
        dto.setRuleId("1");
        dto.setRuleName("rule_1");
        dto.setUser("user");
        dto.setExpireAt(new Date());

        when(ruleStoreService.updateRule(any(RuleDocument.class))).thenReturn(Mono.just(ruleDocumentSample));
        //when
        webTestClient.put()
                .uri("/rule")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), RuleDocumentDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(RuleDocumentDto.class)
                .value(doc -> {
                    System.out.println(doc.toString());
                    assertAll(
                            () -> assertThat(doc.getId()).isEqualTo(ruleDocumentSample.getId()),
                            () -> assertThat(doc.getRuleId()).isEqualTo(ruleDocumentSample.getRuleId()),
                            () -> assertThat(doc.getRuleName()).isEqualTo(ruleDocumentSample.getRuleName()),
                            () -> assertThat(doc.getUser()).isEqualTo(ruleDocumentSample.getUser()),
                            () -> assertThat(doc.getState()).isEqualTo(ruleDocumentSample.getState().name()),
                            () -> assertThat(doc.getExpireAt()).isInstanceOf(Date.class)
                    );
                });
        //then
    }

    @Test
    void update_whenNotFound() {
        //Given
        RuleDocumentDto dto = new RuleDocumentDto();
        dto.setRuleId("1");
        dto.setRuleName("rule_1");
        dto.setUser("user");
        dto.setExpireAt(new Date());

        when(ruleStoreService.updateRule(any(RuleDocument.class))).thenReturn(Mono.error(() -> {
            return new RuleServiceException("Document not Found", ErrorCode.DOCUMENT_NOT_FOUND);
        }));
        //when
        webTestClient.put()
                .uri("/rule")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), RuleDocumentDto.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(JsonNode.class)
                .value(s -> {System.out.println(s);
                    assertThat(s.get("status").asText()).isEqualTo("NOT_FOUND");
                    assertThat(s.get("message").asText()).isEqualTo("Document not Found");
                    assertThat(s.get("exception").asText()).isEqualTo("org.farmtec.store.subscriber.rule.store.exceptions.RuleServiceException");

                });
        //then
    }

    @Test
    void update_whenDataAccessException() {
        //Given
        RuleDocumentDto dto = new RuleDocumentDto();
        dto.setRuleId("1");
        dto.setRuleName("rule_1");
        dto.setUser("user");
        dto.setExpireAt(new Date());

        when(ruleStoreService.updateRule(any(RuleDocument.class))).thenReturn(Mono.error(() -> {
            return new RuleServiceException(new DataAccessResourceFailureException("cant connect to mongo"));
        }));
        //when
        webTestClient.put()
                .uri("/rule")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), RuleDocumentDto.class)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody(JsonNode.class)
                .value(s -> {System.out.println(s);
                    assertThat(s.get("status").asText()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.name());
                    assertThat(s.get("message").asText()).isEqualTo("Storage Service Unavailable");
                    assertThat(s.get("exception").asText()).isEqualTo("org.farmtec.store.subscriber.rule.store.exceptions.RuleServiceException");

                });
        //then
    }

    @Test
    void delete() {
        //Given
        RuleDocumentDto dto = new RuleDocumentDto();
        dto.setRuleId("1");
        dto.setRuleName("rule_1");
        dto.setUser("user");
        dto.setExpireAt(new Date());

        when(ruleStoreService.deleteRule(any(RuleDocument.class))).thenReturn(Mono.just(1L));
        //when
        webTestClient.method(HttpMethod.DELETE)
                .uri("/rule")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(dto), RuleDocumentDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Long.class)
                .value(l -> assertThat(l).isEqualTo(1L));
        //then
    }
}