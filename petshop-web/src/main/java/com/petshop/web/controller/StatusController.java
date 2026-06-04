package com.petshop.web.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StatusController {

    @GetMapping(value = "/test", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String test() {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR"><head><meta charset="UTF-8"/><title>Petshop Web</title>
                <style>
                  body{font-family:Segoe UI,sans-serif;background:#f0fdfa;margin:0;padding:40px;}
                  .box{background:#fff;padding:28px;border-radius:16px;max-width:520px;box-shadow:0 4px 20px rgba(0,0,0,.08);}
                  h1{color:#0d9488;margin:0 0 12px;}
                  p{color:#334155;line-height:1.5;}
                  a{color:#0d9488;font-weight:600;}
                </style></head><body><div class="box">
                <h1>✓ Petshop Web — FUNCIONANDO</h1>
                <p><strong>Porta:</strong> 8090</p>
                <p><a href="/">Ir para o painel completo →</a></p>
                </div></body></html>
                """;
    }
}
