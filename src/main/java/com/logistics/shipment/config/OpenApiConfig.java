package com.logistics.shipment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for OpenAPI/Swagger documentation
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI shipmentTrackingOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");
        
        Contact contact = new Contact();
        contact.setEmail("support@logistics.com");
        contact.setName("Logistics Support Team");
        contact.setUrl("https://www.logistics.com");
        
        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");
        
        Info info = new Info()
                .title("Shipment Tracking API")
                .version("1.0")
                .contact(contact)
                .description("A basic backend service for tracking shipments in a logistics system. " +
                           "This API allows you to create shipments, track their status, and manage " +
                           "status transitions through the logistics workflow.")
                .termsOfService("https://www.logistics.com/terms")
                .license(mitLicense);
        
        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}