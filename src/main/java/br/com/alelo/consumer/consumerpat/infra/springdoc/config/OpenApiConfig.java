package br.com.alelo.consumer.consumerpat.infra.springdoc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(getInfo());
    }

    private Info getInfo() {
        return new Info()
                .title("Consumer Pat API")
                .description("API Rest da aplicação consumer-pat, contendo as funcionalidades de CRUD de clientes, operações com os cartões desses clientes e consultas de extrato desse cartão.")
                .contact(new Contact()
                        .name("DevEx - Experiência ao Desenvolvedor")
                        .email("devex@alelo.com.br")
                );
    }
}
