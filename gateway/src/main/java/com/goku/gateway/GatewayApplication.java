package com.goku.gateway;

import com.goku.gateway.filter.GlobalLogFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Autowired
    private GlobalLogFilter globalLogFilter;

    @Bean
    public RouteLocator defaultRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("route", r -> r.path("/api/orders/**").and().readBody(Object.class, body -> true)
                        .filters(f -> f.filters(globalLogFilter))
                        .uri("http://localhost:8081"))
                .build();
    }
}
