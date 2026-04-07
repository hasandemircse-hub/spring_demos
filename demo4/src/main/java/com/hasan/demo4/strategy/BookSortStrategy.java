package com.hasan.demo4.strategy;

import org.springframework.data.domain.Sort;

// Strategy interface — tüm sıralama algoritmaları bunu implement eder
// Yeni bir sıralama eklemek istersen sadece yeni bir sınıf yazarsın,
// mevcut koda dokunmazsın (Open/Closed Principle)
public interface BookSortStrategy {

    // direction dışarıdan gelir → aynı strateji ASC/DESC çalışabilir
    Sort getSort(Sort.Direction direction);

    // Bu stratejinin hangi isimle çağrılacağı — controller'dan "title", "author" gibi gelir
    String getName();
}
