package com.mugen.backend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotEnvConfig {

    public DotEnvConfig() {
        // Carrega variáveis do .env ANTES do Spring inicializar
        Dotenv dotenv = Dotenv.load();

        // Define como properties do sistema
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
    }
}
