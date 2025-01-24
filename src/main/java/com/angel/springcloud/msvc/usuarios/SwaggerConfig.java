package com.angel.springcloud.msvc.usuarios;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Documentación de la API de Usuarios")
                        .version("1.0.0")
                        .description("API RESTFUL para gestionar los usuarios del sistema")
                        .contact(new Contact()
                                .name("Desarrollador Ángel Gabriel Meneses González")
                                .email("angelin09ozoz@gmail.com")));
    }

}
