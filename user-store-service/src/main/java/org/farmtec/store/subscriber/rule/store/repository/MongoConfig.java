package org.farmtec.store.subscriber.rule.store.repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.bson.UuidRepresentation;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.concurrent.TimeUnit;

/**
 * Created by dp on 29/07/2021
 */
@Configuration
@EnableReactiveMongoRepositories
public class MongoConfig extends AbstractReactiveMongoConfiguration {
    private static final String IP = "127.0.0.1";
    private static final int PORT = 27017;
    private static final String CONNECTION_STRING = "mongodb://%s:%d/test";
    //TODO set proper client

    @Override
    protected String getDatabaseName() {
        return "rules";
    }

    @Override
    protected MongoClientSettings mongoClientSettings() {
        System.out.println("mongoClientSettings WAS INVOQUED !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        builder.applyConnectionString(new ConnectionString(String.format(CONNECTION_STRING, IP, PORT)));
        builder.applyToSocketSettings(socket -> socket.connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS).build());
        builder.applyToConnectionPoolSettings(cnxPool -> cnxPool.maxWaitTime(8, TimeUnit.SECONDS)).build();
        builder.uuidRepresentation(UuidRepresentation.JAVA_LEGACY);
        //this.configureClientSettings(builder);
        return builder.build();
    }

}
