package com.example.laptopshop.domain;

import com.example.laptopshop.util.SecurityUtil;
import com.example.laptopshop.util.constant.GenderEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Name is not allowed to be empty")
    private String name;
    @NotBlank(message = "Email is not allowed to be empty")
    private String email;
    @NotBlank(message = "Password is not allowed to be empty")
    private String password;
    private int age;
    private String address;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get() : null;
        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get() : null;
        this.updatedAt = Instant.now();
    }

}
