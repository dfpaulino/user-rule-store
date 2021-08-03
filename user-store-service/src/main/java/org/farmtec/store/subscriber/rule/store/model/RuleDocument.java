package org.farmtec.store.subscriber.rule.store.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by dp on 29/07/2021
 */
@Document
public class RuleDocument {
    @Id
    private String id;

    @Indexed
    private String ruleId;

    @Indexed
    private String user;

    @Indexed
    private String ruleName;

    private Date createdAt;

    @Indexed(expireAfterSeconds = 360)
    private Date expireAt;

    public String getId() {
        return id;
    }

    public RuleDocument setId(String id) {
        this.id = id;
        return this;
    }

    public String getRuleId() {
        return ruleId;
    }

    public RuleDocument setRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public String getRuleName() {
        return ruleName;
    }

    public RuleDocument setRuleName(String ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public RuleDocument setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public RuleDocument setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
        return this;
    }

    public String getUser() {
        return user;
    }

    public RuleDocument setUser(String user) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        return "RuleDocument{" +
                "id='" + id + '\'' +
                ", ruleId='" + ruleId + '\'' +
                ", user='" + user + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", createdAt=" + createdAt +
                ", expireAt=" + expireAt +
                '}';
    }
}
