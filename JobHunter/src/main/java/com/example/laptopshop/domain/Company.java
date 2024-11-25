package com.example.laptopshop.domain;

import java.time.Instant;

import com.example.laptopshop.util.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Name is not allowed to be empty")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;
    private String logo;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7") // -> chỉ ảnh hưởng đến thuộc tính ngay bên dưới nó
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

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
