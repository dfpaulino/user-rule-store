package org.farmtec.store.subscriber.rule.store.controllers;

import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.farmtec.store.subscriber.rule.store.service.RuleStoreService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


/**
 * Created by dp on 09/08/2021
 */
@Component
public class RuleStoreRequestHandler {

    private final RuleStoreService ruleStoreService;

    public RuleStoreRequestHandler(RuleStoreService ruleStoreService) {
        this.ruleStoreService = ruleStoreService;
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<RuleDocument> requestBodyMono = request.bodyToMono(RuleDocument.class);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBodyMono.flatMap(ruleStoreService::saveRule), RuleDocument.class);
    }

    public Mono<ServerResponse> find(ServerRequest request) {
        RuleDocument searchRuleDocument = buildRuleDocumentFromQueryParameters(request.queryParams());
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body((ruleStoreService.findRule(searchRuleDocument)), RuleDocument.class);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ruleStoreService.findAll(), RuleDocument.class);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Mono<RuleDocument> requestBodyMono = request.bodyToMono(RuleDocument.class);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBodyMono.flatMap(ruleStoreService::updateRule), RuleDocument.class);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Mono<RuleDocument> requestBodyMono = request.bodyToMono(RuleDocument.class);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBodyMono.flatMap(ruleStoreService::deleteRule), Long.class);
    }

    //TODO change this to a proper DTO mapper
    private RuleDocument buildRuleDocumentFromQueryParameters(MultiValueMap<String, String> map) {
        RuleDocument ruleDocument = new RuleDocument();
        ruleDocument.setId(map.getFirst("id"))
                .setRuleId(map.getFirst("ruleId"))
                .setRuleName(map.getFirst("ruleName"))
                .setUser(map.getFirst("user"));
        return ruleDocument;
    }
}
