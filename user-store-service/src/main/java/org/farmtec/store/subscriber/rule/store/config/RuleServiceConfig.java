package org.farmtec.store.subscriber.rule.store.config;

import org.farmtec.store.subscriber.rule.store.repository.MongoUserRuleRepo;
import org.farmtec.store.subscriber.rule.store.repository.UserRuleRepo;
import org.farmtec.store.subscriber.rule.store.service.RuleStoreService;
import org.farmtec.store.subscriber.rule.store.service.RuleStoreServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

/**
 * Created by dp on 10/08/2021
 */
@Configuration
public class RuleServiceConfig {

    @Bean
    public UserRuleRepo userRuleRepo(ReactiveMongoTemplate reactiveMongoTemplate) {
        return new MongoUserRuleRepo(reactiveMongoTemplate);
    }
    @Bean
    public RuleStoreService ruleStoreService(UserRuleRepo repo) {
        return new RuleStoreServiceImpl(repo);
    }
}
