package org.farmtec.store.subscriber.rule.store.service;

import org.farmtec.store.subscriber.rule.store.exceptions.ErrorCode;
import org.farmtec.store.subscriber.rule.store.exceptions.RuleServiceException;
import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.farmtec.store.subscriber.rule.store.repository.MongoGenericDaoImpl;
import org.farmtec.store.subscriber.rule.store.repository.UserRuleRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.logging.Level;

/**
 * Class provides Basic CRUD operations
 * Created by dp on 03/08/2021
 */
public class RuleStoreServiceImpl implements RuleStoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoGenericDaoImpl.class);
    private final UserRuleRepo repo;

    public RuleStoreServiceImpl(UserRuleRepo repo) {
        this.repo = repo;
    }

    /**
     * Save {@link RuleDocument}
     *
     * @param ruleDocument to save
     * @return
     * @throws org.springframework.dao.DataAccessException
     * @throws
     */
    @Override
    public Mono<RuleDocument> saveRule(RuleDocument ruleDocument) {

        return repo.saveDocument(ruleDocument)
                .log(this.getClass().getName(), Level.INFO, SignalType.ON_SUBSCRIBE, SignalType.ON_NEXT, SignalType.ON_COMPLETE)
                //.onErrorResume(original -> {LOGGER.error("Cant store document");
                //    return Mono.error(new RuleServiceException("Unable to save Document",original)});
                .onErrorMap(original -> {
                    LOGGER.error("Cant store document {}", original);
                    return new RuleServiceException("Unable to save Document", original);
                });
    }

    @Override
    public Flux<RuleDocument> findRule(RuleDocument ruleDocument) {

        //Exception if not found
        return repo.findDocument(ruleDocument)
                .onErrorResume((e) -> {
                    LOGGER.error("Exception {}", e);
                    return Flux.error(new RuleServiceException(e));
                })
                .switchIfEmpty(Flux.error(() -> {
                    LOGGER.error("Document Not Found");
                    return new RuleServiceException("Document not Found", ErrorCode.DOCUMENT_NOT_FOUND);
                }));

    }

    @Override
    public Mono<RuleDocument> updateRule(RuleDocument ruleDocument) {
        //Exception if not found
        return repo.updateDocument(ruleDocument)
                .onErrorResume((e) -> {
                    LOGGER.error("Exception {}", e);
                    return Mono.error(new RuleServiceException(e));
                })
                .switchIfEmpty(Mono.error(new RuleServiceException("Document not Found", ErrorCode.DOCUMENT_NOT_FOUND)));
    }

    @Override
    public Mono<Long> deleteRule(RuleDocument ruleDocument) {

        return repo.deleteDocument(ruleDocument);
    }
}
