package org.farmtec.store.subscriber.rule.store.repository;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import de.flapdoodle.embed.mongo.config.*;

import java.util.concurrent.TimeUnit;


/**
 * Created by dp on 29/07/2021
 * Set's up an executable for an embedded Mongo Database with specific IP PORT
 */
@Configuration
@EnableReactiveMongoRepositories
//@ComponentScan(basePackages = "org.farmtec.store")
public class MongoConfigTest {

    private static final String IP = "localhost";
    private static final int PORT = 27017;
    private static final String CONNECTION_STRING = "mongodb://%s:%d/test";

    @Bean
    public IMongodConfig mongodConfig() throws Exception {
      return new MongodConfigBuilder()
              .version(Version.Main.PRODUCTION)
              .net(new Net(IP, PORT, false))
              .build();
    }

    @Bean
    public MongodExecutable mongodExecutable(IMongodConfig config) {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        return starter.prepare(config);
    }
    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(){
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToConnectionPoolSettings(builder -> builder
                        .maxConnectionIdleTime(5, TimeUnit.SECONDS)
                        .build()
                )
                .applyConnectionString(new ConnectionString(String.format(CONNECTION_STRING, IP, PORT)))
                .build();

        ReactiveMongoTemplate template = new ReactiveMongoTemplate(MongoClients.create(settings),"test");
        //throw some exception if any error on read/write
        template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
    return template;
    }

}
