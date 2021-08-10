package org.farmtec.store.subscriber.rule.store.repository;

import de.flapdoodle.embed.mongo.MongodExecutable;
import org.farmtec.store.subscriber.rule.store.config.MongoConfig;
import org.farmtec.store.subscriber.rule.store.config.MongoConnectionProperties;
import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
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
@ContextConfiguration(classes = {MongoConfigTest.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestPropertySource(locations = "classpath:application-test.properties")
//@ConfigurationPropertiesScan("org.farmtec.store.subscriber.rule.store.config")
class MongoUserRuleRepoTest {
    @Autowired
    private MongodExecutable mongodExecutable;

    @Autowired
    private ReactiveMongoTemplate template;

    private MongoUserRuleRepo repo;

    @BeforeEach
    public void cleanData() {
        template.remove(new Query(Criteria.where("user").is("user")), RuleDocument.class).block();
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
    public void save() throws Exception {
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND, 300);
        RuleDocument ruleDocument = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A")
                .setExpireAt(exp.getTime());

        //when
        Mono<RuleDocument> ruleDocumentMono = repo.saveDocument(ruleDocument);
        System.out.println("Just saved" + ruleDocumentMono.block().toString());
        System.out.println("Getting flux");
        //then
        Flux<RuleDocument> ruleDocumentFlux = template.findAll(RuleDocument.class);
        //then
        thenAssertDocument(ruleDocumentFlux, ruleDocument);

    }

    @Test
    //This only works with real mongo
    @Disabled
    void save_whenDuplicate_shouldReturnDuplicateKeyException() throws Exception {
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND, 300);
        RuleDocument ruleDocument = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A")
                .setExpireAt(exp.getTime());
        RuleDocument ruleDocumentDuplicated = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A")
                .setExpireAt(exp.getTime());
        System.out.println("Saving....");
        Mono<RuleDocument> ruleDocumentMono = repo.saveDocument(ruleDocument);
        ruleDocumentMono.block();
        Mono<RuleDocument> ruleDocumentMono2 = repo.saveDocument(ruleDocumentDuplicated);

        //Flux<RuleDocument> savedDocs = template.insertAll(Arrays.asList(ruleDocument, ruleDocument2)); //
        // consume the flux
        ruleDocumentMono2.as(StepVerifier::create) //
                .expectError(DuplicateKeyException.class).verify();
    }

    @Test
    void findById() {
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND, 300);
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
        thenAssertDocument(ruleDocumentFlux, ruleDocument);


    }

    @Test
    void findByUserAndRuleName() throws Exception {
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND, 300);
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
        thenAssertDocument(ruleDocumentFlux, ruleDocument);
    }

    @Test
    void findByUser() throws Exception {
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND, 300);
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

        Flux<RuleDocument> savedDocs = template.insertAll(Arrays.asList(ruleDocument, ruleDocument2)); //
        // consume the flux
        savedDocs.as(StepVerifier::create) //
                .expectNextCount(2) //
                .verifyComplete();
        //savedDocs.blockLast();

        //Thread.sleep(10000);
        //when
        Flux<RuleDocument> ruleDocumentFlux = repo.findDocument(new RuleDocument().setUser(ruleDocument.getUser()));
        System.out.println("Searching ....");
        //then
        StepVerifier.create(ruleDocumentFlux).expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void findByUser_whenNotExist_shouldReturnEmptyFlux() throws Exception {
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND, 300);


        //when
        Flux<RuleDocument> ruleDocumentFlux = repo.findDocument(new RuleDocument().setUser("dummy"));
        System.out.println("Searching ....");
        //then
        StepVerifier.create(ruleDocumentFlux).expectNextCount(0)
                .verifyComplete();

    }

    @Test
    void update() {
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND, 300);
        RuleDocument ruleDocument = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A")
                .setExpireAt(exp.getTime());
        //store document
        RuleDocument savedDoc = template.save(ruleDocument).block();

        System.out.println("-----before---------");
        System.out.println(savedDoc.toString());

        // modify exp date
        exp.add(Calendar.SECOND, 300);
        ruleDocument.setExpireAt(exp.getTime());
        //when
        Mono<RuleDocument> updated = repo.updateDocument(ruleDocument);

        // then
        final String[] id = new String[1];
        StepVerifier.create(updated)
                .assertNext(r -> {
                    System.out.println(r.toString());
                    id[0] = r.getId();
                    assertEquals(exp.getTime(), r.getExpireAt());
                    //assertTrue(r.getExpireAt().after(savedDoc.getExpireAt()));
                })
                .expectComplete()
                .verify();

        //verify the document was updated
        Flux<RuleDocument> findDocuments = template.find(
                new Query(Criteria.where("id").is(id[0])), RuleDocument.class);
        thenAssertDocument(findDocuments, ruleDocument);
    }

    @Test
    void update_whenDocumentNotFound_returnEmptyMono() {
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND, 300);
        RuleDocument ruleDocument = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A")
                .setExpireAt(exp.getTime());
        RuleDocument savedDoc = template.save(ruleDocument).block();
        System.out.println("-----before---------");
        System.out.println(savedDoc.toString());

        //when
        Calendar newExpDate = Calendar.getInstance();
        newExpDate.add(Calendar.HOUR, 24);
        Mono<RuleDocument> updated = repo.updateDocument(new RuleDocument()
                .setId("randomId")
                .setExpireAt(newExpDate.getTime())
        );

        // then
        StepVerifier.create(updated)
                .expectNextCount(0l)
                .verifyComplete();

        //verify the stored document was NOT modified
        Flux<RuleDocument> findDocuments = template.find(
                new Query(Criteria.where("id").is(savedDoc.getId())), RuleDocument.class);
        thenAssertDocument(findDocuments, savedDoc);
    }

    @Test
    void delete() {
        //given
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND, 300);
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

        Flux<RuleDocument> savedDocs = template.insertAll(Arrays.asList(ruleDocument, ruleDocument2)); //
        // consume the flux and ensure there are 2 documents
        savedDocs.as(StepVerifier::create) //
                .expectNextCount(2) //
                .verifyComplete();
        //savedDocs.blockLast();

        //when
        Mono<Long> deletedItems = repo.deleteDocument(ruleDocument);
        //System.out.println("Deleteed " + deletedItems.block()+ "elements");
        StepVerifier.create(deletedItems)
                .assertNext(i -> assertEquals(1L, i))
                .expectComplete()
                .verify();

        //then
        // ensure only 1 document is in store
        Flux<RuleDocument> findDocuments = template.find(
                new Query(Criteria.where("user").is("user")), RuleDocument.class);
        StepVerifier.create(findDocuments).expectNextCount(1L).verifyComplete();
    }

    @Test
    void delete_whenNotInStore() {
        //given
        RuleDocument toDelete = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A");
        //when
        Mono<Long> deletedItems = repo.deleteDocument(toDelete);
        //System.out.println("Deleteed " + deletedItems.block()+ "elements");
        StepVerifier.create(deletedItems)
                .assertNext(i -> assertEquals(0L, i))
                .expectComplete()
                .verify();
    }

    private void thenAssertDocument(Flux<RuleDocument> flux, RuleDocument expectedRuleDocument) {
        StepVerifier.create(flux)
                .assertNext(r -> {
                    assertEquals(expectedRuleDocument.getRuleId(), r.getRuleId());
                    assertEquals(expectedRuleDocument.getRuleName(), r.getRuleName());
                    assertEquals(expectedRuleDocument.getUser(), r.getUser());
                    assertEquals(expectedRuleDocument.getExpireAt(), r.getExpireAt());
                    System.out.println(r);
                })
                .expectComplete()
                .verify();
    }
}