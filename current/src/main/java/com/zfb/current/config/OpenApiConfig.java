package com.zfb.current.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${server.port:8081}")
  private String serverPort;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Current Account API")
                .version("1.0.0")
                .description("Current Account Management API Documentation")
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server().url("http://localhost:" + serverPort).description("Local Server")));
  }
}
