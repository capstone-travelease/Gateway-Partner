package com.GatewayAPI.configuration;


import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class ApplicationRouterValidator {
    public static final List<String> openAPIEndPoints = List.of(
            "/auth/login",
            "/auth/test",
            "/auth/sign"
    );

    public Predicate<ServerHttpRequest> isSecured= request -> openAPIEndPoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
}
