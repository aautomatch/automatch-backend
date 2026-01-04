package com.automatch.portal.records;

import java.time.LocalDateTime;

public record InstructorLicenseTypeRecord(
        String id,
        InstructorRecord instructor,
        ClassifierRecord licenseType,
        LocalDateTime createdAt
) {}
