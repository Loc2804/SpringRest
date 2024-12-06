package com.example.laptopshop.domain.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResEmailJob {
    private long id;
    private String name;
    private double salary;
    private CompanyEmail company;
    private List<SkillEmail> skills;
    private String slug;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CompanyEmail {
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class SkillEmail {
        private String name;
    }
}
