package com.example.laptopshop.domain.dto;

import com.example.laptopshop.util.constant.GenderEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
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
    
    private Instant createdAt;
    private String createdBy;
}
