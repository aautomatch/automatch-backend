package com.automatch.portal.mapper;

import com.automatch.portal.model.DocumentModel;
import com.automatch.portal.records.DocumentRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentMapper {

    public static DocumentRecord toRecord(DocumentModel model) {
        if (model == null) return null;
        return new DocumentRecord(
                model.getId() != null ? model.getId().toString() : null,
                model.getUserId() != null ? model.getUserId().toString() : null,
                model.getDocumentTypeId(),
                model.getDocumentNumber(),
                model.getDocumentImageUrl(),
                model.getIssueDate(),
                model.getExpiryDate(),
                model.getIsVerified(),
                model.getVerifiedByUserId() != null ? model.getVerifiedByUserId().toString() : null,
                model.getVerifiedAt(),
                model.getVerificationNotes(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
        );
    }

    public static DocumentModel fromRecord(DocumentRecord record) {
        if (record == null) return null;
        DocumentModel model = new DocumentModel();

        if (record.id() != null) {
            model.setId(UUID.fromString(record.id()));
        }

        if (record.userId() != null) {
            model.setUserId(UUID.fromString(record.userId()));
        }

        model.setDocumentTypeId(record.documentTypeId());
        model.setDocumentNumber(record.documentNumber());
        model.setDocumentImageUrl(record.documentImageUrl());
        model.setIssueDate(record.issueDate());
        model.setExpiryDate(record.expiryDate());
        model.setIsVerified(record.isVerified());

        if (record.verifiedByUserId() != null) {
            model.setVerifiedByUserId(UUID.fromString(record.verifiedByUserId()));
        }

        model.setVerifiedAt(record.verifiedAt());
        model.setVerificationNotes(record.verificationNotes());
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());

        return model;
    }
}