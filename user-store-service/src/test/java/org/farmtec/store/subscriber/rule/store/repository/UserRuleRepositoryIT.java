package org.farmtec.store.subscriber.rule.store.repository;

import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.MongosConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by dp on 29/07/2021
 */
@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MongoConfigTest.class)
class UserRuleRepositoryIT {
    @Autowired
    private MongodExecutable mongodExecutable;

    @Autowired
    ReactiveMongoTemplate template;

    @BeforeEach
    void setup() throws Exception {
        mongodExecutable.start();
           }
    @AfterEach
    void clean() {
        mongodExecutable.stop();
    }

    @Test
    public void test1() throws Exception{
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.SECOND,300);
        RuleDocument ruleDocument = new RuleDocument()
                .setRuleId("1")
                .setUser("user")
                .setCreatedAt(new Date())
                .setRuleName("A")
                .setExpireAt(exp.getTime());

        Mono<RuleDocument> ruleDocumentMono = template.save(ruleDocument);
        System.out.println("Just saved"+ruleDocumentMono.block().toString());
        //ruleDocumentMono.subscribe(r -> System.out.println("Just saved"+r.toString()));
        System.out.println("Getting flux");
        Flux<RuleDocument> ruleDocumentFlux = template.findAll(RuleDocument.class);
        StepVerifier.create(ruleDocumentFlux)
                .assertNext(r ->{
                        assertEquals("1",r.getRuleId());
                        assertEquals("A", ruleDocument.getRuleName());
                        assertEquals("user",r.getUser());
                        System.out.println(r);
                })
                .expectComplete()
                .verify();

    }

}