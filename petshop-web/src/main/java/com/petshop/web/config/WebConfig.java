package com.petshop.web.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties({GatewayProperties.class, AdminProperties.class, InfraProperties.class})
public class WebConfig implements WebMvcConfigurer {

    private final AdminClienteRedirectInterceptor adminClienteRedirectInterceptor;

    public WebConfig(AdminClienteRedirectInterceptor adminClienteRedirectInterceptor) {
        this.adminClienteRedirectInterceptor = adminClienteRedirectInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminClienteRedirectInterceptor)
                .addPathPatterns("/", "/inicio", "/pets/**", "/agendamentos/**", "/login", "/registro");
    }

    @Bean
    RestClient gatewayRestClient(GatewayProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
