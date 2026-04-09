package com.hasan.demo4.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@Tag(name = "Prayer Times", description = "Namaz vakitleri — aladhan.com proxy (Diyanet method=13)")
@RestController
@RequestMapping("/api/prayer-times")
public class PrayerTimesController {

    private static final String ALADHAN_BASE = "https://api.aladhan.com/v1";

    private final RestTemplate restTemplate;

    public PrayerTimesController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Şehir adına göre bugünkü namaz vakitlerini döner
    // method=13 → Diyanet İşleri Başkanlığı hesaplama yöntemi
    @Operation(summary = "Şehre göre namaz vakitleri", description = "Türkiye'deki şehir adına göre bugünkü namaz vakitlerini döner")
    @SecurityRequirements
    @GetMapping("/{city}")
    public Object getPrayerTimes(@Parameter(description = "Şehir adı (örn: İstanbul, Ankara)") @PathVariable String city) {
        String url = ALADHAN_BASE + "/timingsByCity?city=" + city + "&country=TR&method=13";
        return restTemplate.getForObject(url, Object.class);
    }
}
