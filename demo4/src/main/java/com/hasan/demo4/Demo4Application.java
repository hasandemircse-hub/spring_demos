package com.hasan.demo4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableCaching
@EnableFeignClients  // Feign Client interface'lerini tara ve Spring Bean olarak kaydet
public class Demo4Application {
    public static void main(String[] args) {
        SpringApplication.run(Demo4Application.class, args);
    }

    // RestTemplate → servisler arası HTTP isteği atmak için kullanılır
    // @Bean ile Spring container'a kaydedilir, inject edilebilir hale gelir
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}