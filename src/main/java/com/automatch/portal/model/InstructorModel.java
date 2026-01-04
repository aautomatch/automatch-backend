package com.automatch.portal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorModel {
    private UserModel user;

    private BigDecimal hourlyRate;
    private String bio;
    private Integer yearsExperience = 0;
    private Boolean isVerified = false;

    private BigDecimal averageRating = BigDecimal.valueOf(0.00);
    private Integer totalReviews = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
