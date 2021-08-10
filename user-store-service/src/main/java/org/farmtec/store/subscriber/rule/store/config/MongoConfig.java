package org.farmtec.store.subscriber.rule.store.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.concurrent.TimeUnit;

/**
 * Created by dp on 29/07/2021
 */
@Configuration
@EnableReactiveMongoRepositories
@EnableConfigurationProperties(MongoConnectionProperties.class)
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    private final MongoConnectionProperties mongoConnectionProperties;

    public MongoConfig(MongoConnectionProperties mongoConnectionProperties) {
        this.mongoConnectionProperties = mongoConnectionProperties;
    }
    @Override
    protected String getDatabaseName() {
        return mongoConnectionProperties.getDatabase();
    }

    @Override
    protected MongoClientSettings mongoClientSettings() {
        System.out.println("mongoClientSettings WAS INVOQUED !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        //builder.applyConnectionString(new ConnectionString(String.format(CONNECTION_STRING, IP, PORT)));
        builder.applyConnectionString(new ConnectionString(mongoConnectionProperties.getConnectionString()));
        builder.applyToSocketSettings(socket -> socket.connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS).build());
        builder.applyToConnectionPoolSettings(cnxPool -> cnxPool.maxWaitTime(8, TimeUnit.SECONDS)).build();
        builder.uuidRepresentation(UuidRepresentation.JAVA_LEGACY);
        //this.configureClientSettings(builder);
        return builder.build();
    }

}
