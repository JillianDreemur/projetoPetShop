package com.petshop.gateway;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GatewayStatusConfig {

    @Bean
    RouterFunction<ServerResponse> gatewayStatus() {
        String html = """
                <!DOCTYPE html>
                <html lang="pt-BR"><head><meta charset="UTF-8"/><title>Gateway</title>
                <style>
                  body{font-family:Segoe UI,sans-serif;background:#f0fdfa;margin:0;padding:40px;}
                  .box{background:#fff;padding:28px;border-radius:16px;max-width:520px;box-shadow:0 4px 20px rgba(0,0,0,.08);}
                  h1{color:#0d9488;margin:0 0 12px;}
                  p{color:#334155;line-height:1.5;}
                  code{background:#f1f5f9;padding:2px 8px;border-radius:6px;}
                </style></head><body><div class="box">
                <h1>✓ Gateway — FUNCIONANDO</h1>
                <p><strong>Porta:</strong> 8080</p>
                <p>Rotas: <code>/pets</code>, <code>/agendamentos</code></p>
                <p>Painel web: <a href="http://localhost:8090">http://localhost:8090</a></p>
                </div></body></html>
                """;
        return route(GET("/"), req -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .bodyValue(html))
                .and(route(GET("/test"), req -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .bodyValue(html)));
    }
}
