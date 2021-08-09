package org.farmtec.store.subscriber.rule.store.repository;

import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by dp on 05/08/2021
 */
public interface UserRuleRepo {
    Mono<RuleDocument> saveDocument(org.farmtec.store.subscriber.rule.store.model.RuleDocument ruleDocument);

    Flux<RuleDocument> findDocument(RuleDocument ruleDocument);

    Mono<RuleDocument> updateDocument(RuleDocument ruleDocument);

    Mono<Long> deleteDocument(RuleDocument ruleDocument);
}
