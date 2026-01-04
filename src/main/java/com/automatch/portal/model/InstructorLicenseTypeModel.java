package com.automatch.portal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorLicenseTypeModel {
    private UUID id;
    private InstructorModel instructor;
    private ClassifierModel licenseType;

    private LocalDateTime createdAt;
}
