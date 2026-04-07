package com.hasan.demo4.strategy;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

// Başlığa göre A-Z sıralama stratejisi
// @Component → Spring bu sınıfı otomatik bean olarak kaydeder
@Component
public class TitleSortStrategy implements BookSortStrategy {

    @Override
    public Sort getSort(Sort.Direction direction) {
        return Sort.by(direction, "title");
    }

    @Override
    public String getName() {
        return "title";
    }
}
