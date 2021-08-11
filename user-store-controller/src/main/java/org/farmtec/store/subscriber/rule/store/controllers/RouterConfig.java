package org.farmtec.store.subscriber.rule.store.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

/**
 * Created by dp on 09/08/2021
 */
@Configuration(proxyBeanMethods = false)
public class RouterConfig {
    @Bean
    public RouterFunction<ServerResponse> route(RuleStoreRequestHandler handler) {
        return RouterFunctions.route(POST("/rule").and(contentType(MediaType.APPLICATION_JSON)), handler::save)
                .andRoute(GET("/rules").and(contentType(MediaType.APPLICATION_JSON)), handler::find)
                .andRoute(GET("/rules/all").and(accept(MediaType.APPLICATION_JSON)), handler::getAll)
                .andRoute(PUT("/rule").and(contentType(MediaType.APPLICATION_JSON)), handler::update)
                .andRoute(DELETE("/rule").and(contentType(MediaType.APPLICATION_JSON)), handler::delete);
    }
}
