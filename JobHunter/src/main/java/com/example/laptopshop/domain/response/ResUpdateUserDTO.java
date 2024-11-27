package com.example.laptopshop.domain.response;

import com.example.laptopshop.util.constant.GenderEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private String address;
    private GenderEnum gender;
    private CompanyUser company;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String updatedBy;

    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }
}
