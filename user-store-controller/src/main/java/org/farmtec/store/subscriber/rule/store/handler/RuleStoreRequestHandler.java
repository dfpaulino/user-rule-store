package org.farmtec.store.subscriber.rule.store.handler;

import org.farmtec.store.subscriber.rule.store.dto.RuleDocumentDto;
import org.farmtec.store.subscriber.rule.store.mappers.RuleDocumentMapper;
import org.farmtec.store.subscriber.rule.store.model.RuleDocument;
import org.farmtec.store.subscriber.rule.store.service.RuleStoreService;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


/**
 * Handler Class. This is used by the RouterFunction to dispatch the request
 * to the correct handler/method
 * Created by dp on 09/08/2021
 */
public class RuleStoreRequestHandler {

    private final RuleStoreService ruleStoreService;
    private final RuleDocumentMapper mapper;

    public RuleStoreRequestHandler(RuleStoreService ruleStoreService, RuleDocumentMapper mapper) {
        this.ruleStoreService = ruleStoreService;
        this.mapper = mapper;
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<RuleDocumentDto> requestBodyMono = request.bodyToMono(RuleDocumentDto.class);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBodyMono.map(mapper::toRuleDocument).flatMap(ruleStoreService::saveRule)
                                .map(mapper::toDto), RuleDocumentDto.class);
    }

    public Mono<ServerResponse> find(ServerRequest request) {
        RuleDocument searchRuleDocument = buildRuleDocumentFromQueryParameters(request.queryParams());
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body((ruleStoreService.findRule(searchRuleDocument)).map(mapper::toDto), RuleDocumentDto.class);
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ruleStoreService.findAll().map(mapper::toDto), RuleDocumentDto.class);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Mono<RuleDocumentDto> requestBodyMono = request.bodyToMono(RuleDocumentDto.class);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBodyMono.map(mapper::toRuleDocument).flatMap(ruleStoreService::updateRule).map(mapper::toDto),
                        RuleDocumentDto.class);
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Mono<RuleDocumentDto> requestBodyMono = request.bodyToMono(RuleDocumentDto.class);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBodyMono.map(mapper::toRuleDocument).flatMap(ruleStoreService::deleteRule), Long.class);
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
