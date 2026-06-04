package com.petshop.eureka;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping(value = "/test", produces = MediaType.TEXT_HTML_VALUE)
    public String test() {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR"><head><meta charset="UTF-8"/><title>Eureka</title>
                <style>
                  body{font-family:Segoe UI,sans-serif;background:#f0fdfa;margin:0;padding:40px;}
                  .box{background:#fff;padding:28px;border-radius:16px;max-width:520px;box-shadow:0 4px 20px rgba(0,0,0,.08);}
                  h1{color:#0d9488;margin:0 0 12px;}
                  p{color:#334155;line-height:1.5;}
                  a{color:#0d9488;}
                </style></head><body><div class="box">
                <h1>✓ Eureka — FUNCIONANDO</h1>
                <p><strong>Porta:</strong> 8761</p>
                <p>Painel de serviços: <a href="/">http://localhost:8761/</a></p>
                </div></body></html>
                """;
    }
}
