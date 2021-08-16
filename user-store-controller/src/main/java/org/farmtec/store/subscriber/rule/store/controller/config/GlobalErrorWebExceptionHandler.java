package org.farmtec.store.subscriber.rule.store.controller.config;

import org.farmtec.store.subscriber.rule.store.exceptions.RuleServiceException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.farmtec.store.subscriber.rule.store.exceptions.ErrorCode.DOCUMENT_NOT_FOUND;
import static org.farmtec.store.subscriber.rule.store.exceptions.ErrorCode.STORAGE_ERROR;
import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.*;

/**
 * Exception Handling for all scenarios
 * Created by dp on 10/08/2021
 */
@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(GlobalErrorAttributes g, ApplicationContext applicationContext,
                                          ServerCodecConfigurer serverCodecConfigurer) {
        super(g, new WebProperties.Resources(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(
            ServerRequest request) {

        Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
                //ErrorAttributeOptions.defaults());
                ErrorAttributeOptions.of(EXCEPTION, MESSAGE));

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (getError(request) instanceof RuleServiceException) {
            RuleServiceException ruleServiceException = (RuleServiceException) getError(request);
            if (ruleServiceException.getErrorCode() != null) {
                if (ruleServiceException.getErrorCode() == DOCUMENT_NOT_FOUND) {
                    status = HttpStatus.NOT_FOUND;
                    errorPropertiesMap.put("message", ruleServiceException.getErrorMsg());
                } else if (STORAGE_ERROR == ruleServiceException.getErrorCode()) {
                    status = HttpStatus.SERVICE_UNAVAILABLE;
                    errorPropertiesMap.put("message", "Storage Service Unavailable");
                } else {
                    status = HttpStatus.INTERNAL_SERVER_ERROR;
                    errorPropertiesMap.put("message", "Generic Error");
                }
            }
        } else {
            getError(request).printStackTrace();
        }

        errorPropertiesMap.put("status", status);
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(BodyInserters.fromValue(errorPropertiesMap));
    }
}
