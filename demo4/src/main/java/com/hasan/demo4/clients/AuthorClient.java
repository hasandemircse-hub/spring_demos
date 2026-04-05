package com.hasan.demo4.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hasan.demo4.dto.AuthorDto;

// name: Spring içinde bu Feign bean'inin adı
// url: Author Service'in adresi (application.properties'den okunur)
@FeignClient(name = "author-service", url = "${author.service.url:http://localhost:8081}")
public interface AuthorClient {

    // Author Service'deki endpoint ile birebir aynı imza
    // Feign bu interface'i implement eder — biz HTTP kodu yazmıyoruz
    @GetMapping("/api/authors/search")
    AuthorDto findByName(@RequestParam String name);
}
