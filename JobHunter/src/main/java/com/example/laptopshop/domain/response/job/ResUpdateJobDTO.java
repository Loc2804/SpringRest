package com.example.laptopshop.domain.response.job;

import com.example.laptopshop.util.constant.LevelEnum;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResUpdateJobDTO {
    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private LevelEnum level;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private Instant startDate;
    private Instant endDate;
    private boolean active;

    private Instant updatedAt;
    private String updatedBy;
    private List<String> skills;
}
