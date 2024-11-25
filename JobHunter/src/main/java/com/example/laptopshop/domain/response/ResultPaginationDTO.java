package com.example.laptopshop.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

    @Getter
    @Setter
    public static class Meta {
        private int page; // đang ở trang nào
        private int pageSize; // lấy bao nhiêu phần tử
        private long total; // tổng số phần tử
        private int pages; // tổng số trang
    }
}
