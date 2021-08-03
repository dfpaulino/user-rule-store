package org.farmtec.store.subscriber.rule.store.repository;

import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by dp on 03/08/2021
 */
@Repository
public class MongoUserRuleRepo extends MongoGenericDaoImpl<RuleDocument> {

    public MongoUserRuleRepo(ReactiveMongoTemplate mongoTemplate) {
        super(mongoTemplate, RuleDocument.class);
    }

    public Mono<RuleDocument> saveDocument(RuleDocument ruleDocument) {
        return super.save(ruleDocument);
    }
    public Flux<RuleDocument> findDocument(RuleDocument ruleDocument) {
        Flux<RuleDocument> result;
        Query query = new Query();
        //build the query
        if(null!=ruleDocument.getId()) {
            //get by Id
            query.addCriteria(Criteria.where("id").is(ruleDocument.getId()));
        } else if(null!=ruleDocument.getUser() && null!= ruleDocument.getRuleId()) {
            //findByUserAndRuleId
            query.addCriteria(Criteria.where("user").is(ruleDocument.getUser())
                    .and("ruleId").is(ruleDocument.getRuleId()));
        } else if(null!=ruleDocument.getUser() && null!= ruleDocument.getRuleName()) {
            //findByUserAndRuleName
            query.addCriteria(Criteria.where("user").is(ruleDocument.getUser())
                    .and("ruleName").is(ruleDocument.getRuleName()));
        } else if(null!=ruleDocument.getUser()) {
            //findByUser
            query.addCriteria(Criteria.where("user").is(ruleDocument.getUser()));
        }
        result = super.findByQuery(query);
        return result;
    }
    public Mono<RuleDocument> updateDocument(RuleDocument ruleDocument) {
        return null;
    }
    public Mono<Long> deleteDocument(RuleDocument ruleDocument) {
        return null;
    }

}
