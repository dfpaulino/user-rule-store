package org.farmtec.store.subscriber.rule.store.repository;

import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by dp on 03/08/2021
 */
public class MongoGenericDaoImpl<T>  {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoGenericDaoImpl.class);
    private final ReactiveMongoTemplate mongoTemplate;
    private final Class<T> typeParameterClass;

    protected MongoGenericDaoImpl(ReactiveMongoTemplate mongoTemplate, Class<T> typeParameterClass) {
        this.mongoTemplate = mongoTemplate;
        this.typeParameterClass = typeParameterClass;
    }

    protected Mono<T> save(T t) {
        return mongoTemplate.save(t);
    }

    protected Flux<T> findByQuery(Query query) {
        return  mongoTemplate.find(query,typeParameterClass);
    }

    protected Mono<T> update(Query query, UpdateDefinition update) {
        return mongoTemplate.findAndModify(query,update,typeParameterClass);
    }

    protected Mono<Long> delete(Query query) {
        return mongoTemplate.remove(query,typeParameterClass).map(DeleteResult::getDeletedCount);
    }
}
