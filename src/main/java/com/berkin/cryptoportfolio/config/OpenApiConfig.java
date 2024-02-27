package com.berkin.cryptoportfolio.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Berkin",
                        email = "hello@berkintosun.com",
                        url = "https://www.berkintosun.com"
                ),
                description = "Documentation of Cryptocurrency Portfolio Management System",
                title = "Cryptocurrency Portfolio Management System"
        ),
        servers = {
                @Server(
                        description = "Local Development Environment",
                        url = "http://localhost:8080"
                )
        }
)
public class OpenApiConfig {
}
