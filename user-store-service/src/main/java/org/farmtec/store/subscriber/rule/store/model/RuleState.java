package org.farmtec.store.subscriber.rule.store.model;

/**
 * @Author Daniel Paulino 12/08/2021
 */
public enum RuleState {
    CREATED,
    ACK,
    CONSUMED,
    IGNORED,
    EXPIRED;
}