package com.example.laptopshop.domain.response.resume;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResCreateResume {
    private long id;
    private Instant createdAt;
    private String createdBy;
}
