package com.hasan.demo4.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

// Diyanet sitesi bot isteklerini WAF ile engelliyor.
// aladhan.com: açık kaynak, Diyanet hesaplama yöntemini destekler (method=13)
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
    @GetMapping("/{city}")
    public Object getPrayerTimes(@PathVariable String city) {
        String url = ALADHAN_BASE + "/timingsByCity?city=" + city + "&country=TR&method=13";
        return restTemplate.getForObject(url, Object.class);
    }
}
