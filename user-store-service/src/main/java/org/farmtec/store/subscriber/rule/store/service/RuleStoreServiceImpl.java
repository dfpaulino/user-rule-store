package org.farmtec.store.subscriber.rule.store.service;

import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by dp on 03/08/2021
 */
public class RuleStoreServiceImpl implements RuleStoreService {

    @Override
    public Mono<RuleDocument> saveRule(RuleDocument ruleDocument) {
        return null;
    }

    @Override
    public Flux<RuleDocument> findRule(RuleDocument ruleDocument) {
        return null;
    }

    @Override
    public Mono<RuleDocument> updateRule(RuleDocument ruleDocument) {
        return null;
    }

    @Override
    public Mono<Void> deleteRule(RuleDocument ruleDocument) {
        return null;
    }
}
