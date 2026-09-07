package com.jenjon.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JenjonDeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(JenjonDeliveryApplication.class, args);
    }
}
