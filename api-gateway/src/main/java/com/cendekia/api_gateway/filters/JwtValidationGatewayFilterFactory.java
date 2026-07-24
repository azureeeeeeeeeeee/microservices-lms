package com.cendekia.api_gateway.filters;

import com.cendekia.api_gateway.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtValidationGatewayFilterFactory.Config> {

    private final JwtUtil jwtUtil;

    public JwtValidationGatewayFilterFactory(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);

            try {

                jwtUtil.validateToken(token);

                Claims claims = jwtUtil.extractClaims(token);

                ServerHttpRequest request = exchange.getRequest()
                        .mutate()
                        .headers(headers -> {

                            // Jangan percaya header dari client
                            headers.remove("X-USER-ID");
                            headers.remove("X-USER-EMAIL");
                            headers.remove("X-USER-ROLE");

                            headers.add("X-USER-ID", claims.getSubject());
                            headers.add("X-USER-EMAIL", claims.get("email", String.class));
                            headers.add("X-USER-ROLE", claims.get("role", String.class));
                        })
                        .build();

                return chain.filter(
                        exchange.mutate()
                                .request(request)
                                .build()
                );

            } catch (Exception e) {
                return unauthorized(exchange);
            }
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}