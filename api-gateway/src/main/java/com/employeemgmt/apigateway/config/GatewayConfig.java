package com.employeemgmt.apigateway.config;

import com.employeemgmt.apigateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth Service Routes (No JWT needed)
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service"))

                // Employee Service Routes (JWT required)
                .route("employee-service-employees", r -> r
                        .path("/api/employees/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter))
                        .uri("lb://employee-service"))

                .route("employee-service-departments", r -> r
                        .path("/api/departments/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtFilter))
                        .uri("lb://employee-service"))

                .build();
    }
}