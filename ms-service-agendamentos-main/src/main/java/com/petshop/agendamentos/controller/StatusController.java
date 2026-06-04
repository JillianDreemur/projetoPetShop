package com.petshop.agendamentos.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping(value = {"/", "/test"}, produces = MediaType.TEXT_HTML_VALUE)
    public String status() {
        return html("Service Agendamentos", 8082, "API REST em /agendamentos");
    }

    private static String html(String nome, int porta, String extra) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR"><head><meta charset="UTF-8"/><title>%s</title>
                <style>
                  body{font-family:Segoe UI,sans-serif;background:#f0fdfa;margin:0;padding:40px;}
                  .box{background:#fff;padding:28px;border-radius:16px;max-width:520px;box-shadow:0 4px 20px rgba(0,0,0,.08);}
                  h1{color:#0d9488;margin:0 0 12px;font-size:1.6rem;}
                  p{color:#334155;line-height:1.5;}
                  code{background:#f1f5f9;padding:2px 8px;border-radius:6px;}
                </style></head><body><div class="box">
                <h1>✓ %s — FUNCIONANDO</h1>
                <p><strong>Porta:</strong> %d</p>
                <p><strong>Info:</strong> %s</p>
                <p>Teste a API: <code>GET http://localhost:%d/agendamentos</code></p>
                </div></body></html>
                """.formatted(nome, nome, porta, extra, porta);
    }
}
