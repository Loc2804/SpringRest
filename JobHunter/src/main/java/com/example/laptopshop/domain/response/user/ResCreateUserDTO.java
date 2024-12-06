package com.example.laptopshop.domain.response.user;

import com.example.laptopshop.util.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private String address;
    private GenderEnum gender;
    private CompanyUser company;
    private RoleUser role;
    private Instant createdAt;
    private String createdBy;

    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class RoleUser{
        private long id;
        private String name;
    }
}
