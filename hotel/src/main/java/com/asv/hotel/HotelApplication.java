package com.asv.hotel;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@OpenAPIDefinition(
        info = @Info(
                title = "Hotel API",
                version = "1.0",
                description = "API для управления"
        )
)

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.asv.hotel.repositories")
@AllArgsConstructor
@EnableDiscoveryClient
public class HotelApplication {

    public static void main(String[] args) {

        SpringApplication.run(HotelApplication.class, args);

    }


}
