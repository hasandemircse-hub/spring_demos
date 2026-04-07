package com.hasan.demo4.strategy;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

// Yazara göre A-Z sıralama stratejisi
@Component
public class AuthorSortStrategy implements BookSortStrategy {

    @Override
    public Sort getSort(Sort.Direction direction) {
        return Sort.by(direction, "author");
    }

    @Override
    public String getName() {
        return "author";
    }
}
