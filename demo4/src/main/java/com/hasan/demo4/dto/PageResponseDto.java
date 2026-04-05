package com.hasan.demo4.dto;

import java.util.List;

// Sayfalı yanıtı taşır — içindeki veri tipi generic (T) çünkü
// ileride Book dışındaki entity'ler için de kullanılabilsin
public class PageResponseDto<T> {

    private List<T> content;       // bu sayfadaki kayıtlar
    private int page;              // mevcut sayfa numarası (0'dan başlar)
    private int size;              // sayfadaki kayıt sayısı
    private long totalElements;    // DB'deki toplam kayıt sayısı
    private int totalPages;        // toplam sayfa sayısı
    private boolean last;          // bu son sayfa mı?

    public PageResponseDto(List<T> content, int page, int size,
                           long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    public List<T> getContent() { return content; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isLast() { return last; }
}
