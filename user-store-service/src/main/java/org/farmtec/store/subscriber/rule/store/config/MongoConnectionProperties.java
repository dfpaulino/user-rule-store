package org.farmtec.store.subscriber.rule.store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by dp on 09/08/2021
 */
@ConfigurationProperties(prefix = "mongodb")
public class MongoConnectionProperties {

    private String connectionString = "mongodb://localhost:27017/rules";
    private String database = "rules";

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
