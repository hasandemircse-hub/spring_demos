package com.hasan.demo4.strategy;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

// ID'ye göre sıralama stratejisi (default)
@Component
public class IdSortStrategy implements BookSortStrategy {

    @Override
    public Sort getSort(Sort.Direction direction) {
        return Sort.by(direction, "id");
    }

    @Override
    public String getName() {
        return "id";
    }
}
