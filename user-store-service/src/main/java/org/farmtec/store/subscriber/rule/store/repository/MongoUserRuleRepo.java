package org.farmtec.store.subscriber.rule.store.repository;

import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by dp on 03/08/2021
 */
@Repository
public class MongoUserRuleRepo extends MongoGenericDaoImpl<RuleDocument> implements UserRuleRepo {

    public MongoUserRuleRepo(ReactiveMongoTemplate mongoTemplate) {
        super(mongoTemplate, RuleDocument.class);
    }

    /**
     * Save {@link RuleDocument}
     *
     * @param ruleDocument to save
     * @return
     */
    public Mono<RuleDocument> saveDocument(RuleDocument ruleDocument) {
        return super.save(ruleDocument);
    }

    /**
     * Find  {@link RuleDocument}'s based on properties provided
     * The criteria is based on {@link RuleDocument}
     *
     * @param ruleDocument sample to search
     * @return {@link Flux<RuleDocument>}
     */
    public Flux<RuleDocument> findDocument(RuleDocument ruleDocument) {
        Flux<RuleDocument> result;

        result = super.findByQuery(validateAndBuildQuery(ruleDocument, true));
        return result;
    }


    /**
     * Update a {@link RuleDocument}.
     * The update is based on ID Or user & RuleId
     * Only update Expiry time
     *
     * @param ruleDocument
     * @return {@link Mono<RuleDocument>} with the updated version
     */
    public Mono<RuleDocument> updateDocument(RuleDocument ruleDocument) {
        Update update = new Update();
        update.set("expireAt", ruleDocument.getExpireAt());

        return super.update(validateAndBuildQuery(ruleDocument, false), update)
                .map(r -> r.setExpireAt(ruleDocument.getExpireAt()));
    }

    /**
     * Delete a {@link RuleDocument} based on properties provided
     * The criteria is based on {@link RuleDocument} properties
     *
     * @param ruleDocument
     * @return Mono with the count of deleted items
     */
    public Mono<Long> deleteDocument(RuleDocument ruleDocument) {
        return super.delete(validateAndBuildQuery(ruleDocument, false));
    }

    /**
     * The criteria is based on {@link RuleDocument} properties, with the following priorities
     * <ul>
     *     <li>id</li>
     *     <li>user & ruleId</li>
     *     <li>user & ruleName</li>
     *     <li>user</li>
     * </ul>
     *
     * @param SearchRuleDocument    {@link RuleDocument} with the search properties
     * @param allowSearchByUserOnly <P>Allow to search only by user. this can result in multiple document</P>
     *                              <P>Search by user is only allowed if allowSearchByUserOnly flag is true</P>
     * @return {@link Query}
     */

    //TODO Add validation and throw Exception if validation fails
    private Query validateAndBuildQuery(RuleDocument SearchRuleDocument, boolean allowSearchByUserOnly) {
        Query query = new Query();
        if (null != SearchRuleDocument.getId()) {
            //get by Id
            query.addCriteria(Criteria.where("id").is(SearchRuleDocument.getId()));
        } else if (null != SearchRuleDocument.getUser() && null != SearchRuleDocument.getRuleId()) {
            //findByUserAndRuleId
            query.addCriteria(Criteria.where("user").is(SearchRuleDocument.getUser())
                    .and("ruleId").is(SearchRuleDocument.getRuleId()));
        } else if (null != SearchRuleDocument.getUser() && null != SearchRuleDocument.getRuleName()) {
            //findByUserAndRuleName
            query.addCriteria(Criteria.where("user").is(SearchRuleDocument.getUser())
                    .and("ruleName").is(SearchRuleDocument.getRuleName()));
        } else if (null != SearchRuleDocument.getUser() && allowSearchByUserOnly) {
            //findByUser
            query.addCriteria(Criteria.where("user").is(SearchRuleDocument.getUser()));
        } else {
            throw new RuntimeException("Cant build query");
        }
        return query;
    }

}
