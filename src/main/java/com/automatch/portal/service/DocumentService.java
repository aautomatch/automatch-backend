package com.automatch.portal.service;

import com.automatch.portal.dao.DocumentDAO;
import com.automatch.portal.mapper.DocumentMapper;
import com.automatch.portal.model.DocumentModel;
import com.automatch.portal.records.DocumentRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentDAO documentDAO;

    @Transactional
    public DocumentRecord save(DocumentRecord documentRecord) {
        validateDocumentRecord(documentRecord);

        DocumentModel documentModel = DocumentMapper.fromRecord(documentRecord);

        if (documentModel.getId() == null) {
            return createDocument(documentModel);
        } else {
            return updateDocument(documentRecord.id(), documentRecord);
        }
    }

    private DocumentRecord createDocument(DocumentModel documentModel) {
        // Verificar se o usuário já tem um documento deste tipo
        if (documentDAO.existsByUserAndType(documentModel.getUserId(), documentModel.getDocumentTypeId())) {
            throw new IllegalArgumentException("User already has a document of this type");
        }

        // Verificar se o número do documento já existe
        if (documentDAO.existsByDocumentNumber(documentModel.getDocumentNumber())) {
            throw new IllegalArgumentException("Document number already exists: " + documentModel.getDocumentNumber());
        }

        // Validar datas
        validateDocumentDates(documentModel.getIssueDate(), documentModel.getExpiryDate());

        documentModel.setIsVerified(false);
        documentModel.setCreatedAt(LocalDateTime.now());
        documentModel.setUpdatedAt(LocalDateTime.now());

        DocumentModel savedModel = documentDAO.save(documentModel);
        return DocumentMapper.toRecord(savedModel);
    }

    public DocumentRecord getById(String id) {
        UUID uuid = UUID.fromString(id);
        return documentDAO.findById(uuid)
                .map(DocumentMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));
    }

    public List<DocumentRecord> getAll() {
        return documentDAO.findAll().stream()
                .map(DocumentMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<DocumentRecord> getByUser(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return documentDAO.findByUser(userUuid).stream()
                .map(DocumentMapper::toRecord)
                .collect(Collectors.toList());
    }

    public DocumentRecord getByUserAndType(String userId, Integer documentTypeId) {
        UUID userUuid = UUID.fromString(userId);
        return documentDAO.findByUserAndType(userUuid, documentTypeId)
                .map(DocumentMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Document not found for user ID: " + userId + " and type ID: " + documentTypeId));
    }

    public List<DocumentRecord> getByType(Integer documentTypeId) {
        return documentDAO.findByType(documentTypeId).stream()
                .map(DocumentMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<DocumentRecord> getVerifiedDocuments() {
        return documentDAO.findVerified().stream()
                .map(DocumentMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<DocumentRecord> getPendingVerificationDocuments() {
        return documentDAO.findPendingVerification().stream()
                .map(DocumentMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<DocumentRecord> getExpiringSoonDocuments(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be a positive number");
        }

        LocalDate thresholdDate = LocalDate.now().plusDays(days);
        return documentDAO.findExpiringSoon(thresholdDate).stream()
                .map(DocumentMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<DocumentRecord> getExpiredDocuments() {
        return documentDAO.findExpired().stream()
                .map(DocumentMapper::toRecord)
                .collect(Collectors.toList());
    }

    public DocumentRecord getByDocumentNumber(String documentNumber) {
        return documentDAO.findByDocumentNumber(documentNumber)
                .map(DocumentMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with number: " + documentNumber));
    }

    @Transactional
    public void delete(String id) {
        UUID uuid = UUID.fromString(id);
        DocumentModel document = documentDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));

        if (document.getDeletedAt() != null) {
            throw new IllegalArgumentException("Document is already deleted");
        }

        boolean deleted = documentDAO.delete(uuid);
        if (!deleted) {
            throw new RuntimeException("Failed to delete document with ID: " + id);
        }
    }

    @Transactional
    public void restore(String id) {
        UUID uuid = UUID.fromString(id);
        DocumentModel document = documentDAO.findByIdWithDeleted(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));

        if (document.getDeletedAt() == null) {
            throw new IllegalArgumentException("Document is not deleted");
        }

        boolean restored = documentDAO.restore(uuid);
        if (!restored) {
            throw new RuntimeException("Failed to restore document with ID: " + id);
        }
    }

    @Transactional
    public DocumentRecord verifyDocument(String id, String verifiedByUserId, String notes) {
        UUID uuid = UUID.fromString(id);
        UUID verifierUuid = UUID.fromString(verifiedByUserId);

        DocumentModel document = documentDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));

        if (document.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot verify a deleted document");
        }

        if (Boolean.TRUE.equals(document.getIsVerified())) {
            throw new IllegalArgumentException("Document is already verified");
        }

        // Verificar se o documento está expirado
        if (isDocumentExpired(document)) {
            throw new IllegalArgumentException("Cannot verify an expired document");
        }

        document.setIsVerified(true);
        document.setVerifiedByUserId(verifierUuid);
        document.setVerifiedAt(LocalDateTime.now());
        document.setVerificationNotes(notes);
        document.setUpdatedAt(LocalDateTime.now());

        DocumentModel updatedModel = documentDAO.save(document);
        return DocumentMapper.toRecord(updatedModel);
    }

    @Transactional
    public DocumentRecord unverifyDocument(String id) {
        UUID uuid = UUID.fromString(id);

        DocumentModel document = documentDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));

        if (document.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot unverify a deleted document");
        }

        if (Boolean.FALSE.equals(document.getIsVerified())) {
            throw new IllegalArgumentException("Document is already unverified");
        }

        document.setIsVerified(false);
        document.setVerifiedByUserId(null);
        document.setVerifiedAt(null);
        document.setVerificationNotes(null);
        document.setUpdatedAt(LocalDateTime.now());

        DocumentModel updatedModel = documentDAO.save(document);
        return DocumentMapper.toRecord(updatedModel);
    }

    @Transactional
    public DocumentRecord updateDocument(String id, DocumentRecord documentRecord) {
        validateDocumentRecord(documentRecord);

        UUID uuid = UUID.fromString(id);
        DocumentModel existingDocument = documentDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));

        if (existingDocument.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update a deleted document");
        }

        // Não permitir alterar o tipo de documento
        if (!existingDocument.getDocumentTypeId().equals(documentRecord.documentTypeId())) {
            throw new IllegalArgumentException("Cannot change document type");
        }

        // Verificar se o novo número de documento já existe (exceto para este documento)
        if (!existingDocument.getDocumentNumber().equals(documentRecord.documentNumber()) &&
                documentDAO.existsByDocumentNumber(documentRecord.documentNumber())) {
            throw new IllegalArgumentException("Document number already exists: " + documentRecord.documentNumber());
        }

        // Validar datas
        validateDocumentDates(documentRecord.issueDate(), documentRecord.expiryDate());

        DocumentModel updatedModel = DocumentMapper.fromRecord(documentRecord);
        updatedModel.setId(uuid);
        updatedModel.setCreatedAt(existingDocument.getCreatedAt());
        updatedModel.setUpdatedAt(LocalDateTime.now());
        updatedModel.setUserId(existingDocument.getUserId()); // Preservar userId

        // Preservar campos de verificação
        if (updatedModel.getIsVerified() == null) {
            updatedModel.setIsVerified(existingDocument.getIsVerified());
        }

        if (updatedModel.getVerifiedByUserId() == null) {
            updatedModel.setVerifiedByUserId(existingDocument.getVerifiedByUserId());
        }

        if (updatedModel.getVerifiedAt() == null) {
            updatedModel.setVerifiedAt(existingDocument.getVerifiedAt());
        }

        if (updatedModel.getVerificationNotes() == null) {
            updatedModel.setVerificationNotes(existingDocument.getVerificationNotes());
        }

        DocumentModel savedModel = documentDAO.save(updatedModel);
        return DocumentMapper.toRecord(savedModel);
    }

    public List<DocumentRecord> searchDocuments(String documentNumber, Integer documentTypeId, Boolean isVerified) {
        return documentDAO.search(documentNumber, documentTypeId, isVerified).stream()
                .map(DocumentMapper::toRecord)
                .collect(Collectors.toList());
    }

    public Object getUserDocumentStats(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return documentDAO.getUserDocumentStats(userUuid);
    }

    public Object getVerifierStats(String verifierId) {
        UUID verifierUuid = UUID.fromString(verifierId);
        return documentDAO.getVerifierStats(verifierUuid);
    }

    public boolean isDocumentExpired(String id) {
        UUID uuid = UUID.fromString(id);
        DocumentModel document = documentDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));

        return isDocumentExpired(document);
    }

    private boolean isDocumentExpired(DocumentModel document) {
        if (document.getExpiryDate() == null) {
            return false; // Documento sem data de expiração
        }
        return document.getExpiryDate().isBefore(LocalDate.now());
    }

    public boolean validateDocument(String id) {
        UUID uuid = UUID.fromString(id);
        DocumentModel document = documentDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));

        // Um documento é válido se:
        // 1. Não está deletado
        // 2. Está verificado
        // 3. Não está expirado (se tiver data de expiração)
        if (document.getDeletedAt() != null) {
            return false;
        }

        if (!Boolean.TRUE.equals(document.getIsVerified())) {
            return false;
        }

        if (document.getExpiryDate() != null && document.getExpiryDate().isBefore(LocalDate.now())) {
            return false;
        }

        return true;
    }

    @Transactional
    public DocumentRecord updateExpiryDate(String id, LocalDate newExpiryDate) {
        UUID uuid = UUID.fromString(id);

        DocumentModel document = documentDAO.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + id));

        if (document.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot update expiry date of a deleted document");
        }

        // Validar que a nova data de expiração é depois da data de emissão
        if (document.getIssueDate() != null && newExpiryDate.isBefore(document.getIssueDate())) {
            throw new IllegalArgumentException("Expiry date must be after issue date");
        }

        document.setExpiryDate(newExpiryDate);
        document.setUpdatedAt(LocalDateTime.now());

        // Se a data de expiração foi alterada para uma data no passado, desverificar
        if (newExpiryDate.isBefore(LocalDate.now()) && Boolean.TRUE.equals(document.getIsVerified())) {
            document.setIsVerified(false);
            document.setVerifiedByUserId(null);
            document.setVerifiedAt(null);
            document.setVerificationNotes("Auto-unverified due to expiry date change");
        }

        DocumentModel updatedModel = documentDAO.save(document);
        return DocumentMapper.toRecord(updatedModel);
    }

    public int countByUser(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return documentDAO.countByUser(userUuid);
    }

    public int countVerifiedByUser(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return documentDAO.countVerifiedByUser(userUuid);
    }

    private void validateDocumentRecord(DocumentRecord documentRecord) {
        if (documentRecord == null) {
            throw new IllegalArgumentException("Document record cannot be null");
        }

        if (documentRecord.userId() == null || documentRecord.userId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (documentRecord.documentTypeId() == null) {
            throw new IllegalArgumentException("Document type ID is required");
        }

        if (documentRecord.documentNumber() == null || documentRecord.documentNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Document number is required");
        }

        if (documentRecord.documentImageUrl() == null || documentRecord.documentImageUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Document image URL is required");
        }
    }

    private void validateDocumentDates(LocalDate issueDate, LocalDate expiryDate) {
        if (issueDate != null && issueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Issue date cannot be in the future");
        }

        if (expiryDate != null && issueDate != null && expiryDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Expiry date must be after issue date");
        }

        if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiry date cannot be in the past for new documents");
        }
    }
}