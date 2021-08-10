package org.farmtec.store.subscriber.rule.store.service;

import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by dp on 03/08/2021
 */
public interface RuleStoreService {
    Mono<RuleDocument> saveRule(RuleDocument ruleDocument);

    Flux<RuleDocument> findRule(RuleDocument ruleDocument);
    Flux<RuleDocument> findAll();
    Mono<RuleDocument> updateRule(RuleDocument ruleDocument);

    Mono<Long> deleteRule(RuleDocument ruleDocument);
}
