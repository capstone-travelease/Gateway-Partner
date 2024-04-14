package com.GatewayAPI.configuration;

import com.GatewayAPI.Utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import java.util.HashMap;
import java.util.Map;

@Component
public class ApplicationAuthenticationFilter  extends AbstractGatewayFilterFactory<ApplicationAuthenticationFilter.Config> {

    @Autowired
    private ApplicationRouterValidator applicationRouterValidator;


    @Autowired
    private JwtUtil jwtUtil;

    public ApplicationAuthenticationFilter() {
        super(Config.class);

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            if (applicationRouterValidator.isSecured.test(exchange.getRequest())) {
                ServerHttpResponse response = exchange.getResponse();
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    try {
                        return handleError(response);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                try {
                  jwtUtil.isTokenValid(authHeader);
                } catch (Exception e) {
                    try {
                        return handleError(response);
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            return chain.filter(exchange);
        }));
    }
    private Mono<Void> handleError(ServerHttpResponse response) throws JsonProcessingException {

        Map<String, Object> errBody = new HashMap<>();
        errBody.put("code",401);
        errBody.put("error","UNAUTHORIZED");
        errBody.put("message","EXPIRED_TOKEN");
        // Set the HTTP status code and response body
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        DataBuffer buffer = response.bufferFactory().wrap(new ObjectMapper().writeValueAsBytes(errBody));

        // Return a Mono representing the completion of the error response
        return response.writeWith(Mono.just(buffer));
    }
    public static class Config{

    }

}
