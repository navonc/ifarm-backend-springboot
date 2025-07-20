package com.ifarm.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger API文档配置
 * 
 * @author ifarm
 * @since 2025-01-17
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("iFarm 电子农场认养系统 API")
                        .description("基于Spring Boot的电子农场认养系统后端API接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("iFarm Team")
                                .email("contact@ifarm.com")
                                .url("https://www.ifarm.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8081/api").description("开发环境"),
                        new Server().url("https://api.ifarm.com").description("生产环境")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT认证，请在下方输入Token值（不需要Bearer前缀）")));
    }
}