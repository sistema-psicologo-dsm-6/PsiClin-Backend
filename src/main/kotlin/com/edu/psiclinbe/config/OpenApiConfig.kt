package com.edu.psiclinbe.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "PsiClin API",
        version = "1.0.0",
        description = "API para gestão clínica: autenticação, funcionários, equipes, agenda, prontuários e pagamentos.",
        contact = Contact(name = "PsiClin"),
    ),
    servers = [
        Server(url = "http://localhost:8080", description = "Servidor local"),
        Server(url = "https://psiclin-backend.onrender.com/", description = "Servidor de produção"),
    ],
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
)
class OpenApiConfig
