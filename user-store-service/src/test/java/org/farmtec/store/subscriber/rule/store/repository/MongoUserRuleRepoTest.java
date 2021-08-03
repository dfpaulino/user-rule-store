package org.farmtec.store.subscriber.rule.store.repository;

import de.flapdoodle.embed.mongo.MongodExecutable;
import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by dp on 03/08/2021
 */
@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoConfigTest.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoUserRuleRepoTest {
    @Autowired
    private MongodExecutable mongodExecutable;

    @Autowired
    private ReactiveMongoTemplate template;

    private MongoUserRuleRepo repo;

    @BeforeEach
    public void cleanData() {
        template.remove(new Query(Criteria.where("user").is("user")),RuleDocument.class).block();
    }
    @BeforeAll
    public void setup() throws Exception {
        mongodExecutable.start();
        repo = new MongoUserRuleRepo(template);
    }
    @AfterAll
    void clean() {
        mongodExecutable.stop();
    }

    private void start() {

    }

    @Test
    public void save() throws Exception{
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND,300);
        RuleDocument ruleDocument = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A")
                .setExpireAt(exp.getTime());

        //when
        Mono<RuleDocument> ruleDocumentMono = repo.saveDocument(ruleDocument);
        System.out.println("Just saved"+ruleDocumentMono.block().toString());
        System.out.println("Getting flux");
        //then
        Flux<RuleDocument> ruleDocumentFlux = template.findAll(RuleDocument.class);
        //then
        thenAssertDocument(ruleDocumentFlux,ruleDocument);

    }

    @Test
    void findById() {
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND,300);
        RuleDocument ruleDocument = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A")
                .setExpireAt(exp.getTime());
        Mono<RuleDocument> savedDocMono = template.save(ruleDocument);
        RuleDocument toFind = new RuleDocument().setId(savedDocMono.block().getId());
        //when

        Flux<RuleDocument> ruleDocumentFlux = repo.findDocument(toFind);
        //then
        thenAssertDocument(ruleDocumentFlux,ruleDocument);


    }

    @Test
    void findByUserAndRuleName() throws Exception{
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND,300);
        RuleDocument ruleDocument = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A")
                .setExpireAt(exp.getTime());
        RuleDocument savedDoc = template.save(ruleDocument).block();
        RuleDocument toFind = new RuleDocument()
                .setUser(savedDoc.getUser())
                .setRuleName(savedDoc.getRuleName());
        //when
        Flux<RuleDocument> ruleDocumentFlux = repo.findDocument(toFind);
        //then
        thenAssertDocument(ruleDocumentFlux,ruleDocument);
    }

    @Test
    void findByUser() throws Exception{
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND,300);
        RuleDocument ruleDocument = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A")
                .setExpireAt(exp.getTime());
        RuleDocument ruleDocument2 = new RuleDocument()
                .setRuleId("2")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("B")
                .setExpireAt(exp.getTime());
        System.out.println("Saving....");

        Flux<RuleDocument> savedDocs = template.insertAll(Arrays.asList(ruleDocument,ruleDocument2)); //
        savedDocs.as(StepVerifier::create) //
                .expectNextCount(2) //
                .verifyComplete();
        //savedDocs.blockLast();

        //when
        Flux<RuleDocument> ruleDocumentFlux = repo.findDocument(new RuleDocument().setUser(ruleDocument.getUser()));
        System.out.println("Searching ....");
        //then
        StepVerifier.create(ruleDocumentFlux).expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    private void thenAssertDocument(Flux<RuleDocument> flux,RuleDocument expectedRuleDocument) {
        StepVerifier.create(flux)
                .assertNext(r ->{
                    assertEquals(expectedRuleDocument.getRuleId(),r.getRuleId());
                    assertEquals(expectedRuleDocument.getRuleName(), r.getRuleName());
                    assertEquals(expectedRuleDocument.getUser(),r.getUser());
                    System.out.println(r);
                })
                .expectComplete()
                .verify();
    }
}