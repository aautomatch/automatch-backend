package com.automatch.portal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentModel {
    private UUID id;
    private UUID userId;
    private Integer documentTypeId;
    private String documentNumber;
    private String documentImageUrl;

    private LocalDate issueDate;
    private LocalDate expiryDate;

    private Boolean isVerified = false;
    private UUID verifiedByUserId;
    private LocalDateTime verifiedAt;
    private String verificationNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
