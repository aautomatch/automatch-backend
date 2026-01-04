package com.automatch.portal.mapper;

import com.automatch.portal.model.DocumentModel;
import com.automatch.portal.records.DocumentRecord;

import java.util.UUID;

public class DocumentMapper {

    public static DocumentRecord toRecord(DocumentModel model) {
        if (model == null) return null;
        return new DocumentRecord(
                model.getId().toString(),
                UserMapper.toRecord(model.getUser()),
                ClassifierMapper.toRecord(model.getDocumentType()),
                model.getDocumentNumber(),
                model.getDocumentImageUrl(),
                model.getIssueDate(),
                model.getExpiryDate(),
                model.getIsVerified(),
                UserMapper.toRecord(model.getVerifiedBy()),
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
        model.setId(UUID.fromString(record.id()));
        model.setUser(UserMapper.fromRecord(record.user()));
        model.setDocumentType(ClassifierMapper.fromRecord(record.documentType()));
        model.setDocumentNumber(record.documentNumber());
        model.setDocumentImageUrl(record.documentImageUrl());
        model.setIssueDate(record.issueDate());
        model.setExpiryDate(record.expiryDate());
        model.setIsVerified(record.isVerified());
        model.setVerifiedBy(UserMapper.fromRecord(record.verifiedBy()));
        model.setVerifiedAt(record.verifiedAt());
        model.setVerificationNotes(record.verificationNotes());
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());
        return model;
    }
}
