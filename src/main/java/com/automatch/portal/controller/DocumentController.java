package com.automatch.portal.controller;

import com.automatch.portal.records.DocumentRecord;
import com.automatch.portal.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/protected/document")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentRecord> createDocument(@RequestBody DocumentRecord documentRecord) {
        DocumentRecord createdDocument = documentService.save(documentRecord);
        return ResponseEntity.ok(createdDocument);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentRecord> getDocumentById(@PathVariable String id) {
        DocumentRecord document = documentService.getById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping
    public ResponseEntity<List<DocumentRecord>> getAllDocuments() {
        List<DocumentRecord> documents = documentService.getAll();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DocumentRecord>> getDocumentsByUser(@PathVariable String userId) {
        List<DocumentRecord> documents = documentService.getByUser(userId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/user/{userId}/type/{documentTypeId}")
    public ResponseEntity<DocumentRecord> getDocumentByUserAndType(
            @PathVariable String userId,
            @PathVariable Integer documentTypeId) {
        DocumentRecord document = documentService.getByUserAndType(userId, documentTypeId);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/type/{documentTypeId}")
    public ResponseEntity<List<DocumentRecord>> getDocumentsByType(@PathVariable Integer documentTypeId) {
        List<DocumentRecord> documents = documentService.getByType(documentTypeId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/verified")
    public ResponseEntity<List<DocumentRecord>> getVerifiedDocuments() {
        List<DocumentRecord> documents = documentService.getVerifiedDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/pending-verification")
    public ResponseEntity<List<DocumentRecord>> getPendingVerificationDocuments() {
        List<DocumentRecord> documents = documentService.getPendingVerificationDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<List<DocumentRecord>> getExpiringSoonDocuments(
            @RequestParam(defaultValue = "30") int days) {
        List<DocumentRecord> documents = documentService.getExpiringSoonDocuments(days);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<DocumentRecord>> getExpiredDocuments() {
        List<DocumentRecord> documents = documentService.getExpiredDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/number/{documentNumber}")
    public ResponseEntity<DocumentRecord> getDocumentByNumber(@PathVariable String documentNumber) {
        DocumentRecord document = documentService.getByDocumentNumber(documentNumber);
        return ResponseEntity.ok(document);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentRecord> updateDocument(@PathVariable String id, @RequestBody DocumentRecord documentRecord) {
        DocumentRecord updatedDocument = documentService.updateDocument(id, documentRecord);
        return ResponseEntity.ok(updatedDocument);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<DocumentRecord> verifyDocument(
            @PathVariable String id,
            @RequestParam String verifiedByUserId,
            @RequestParam(required = false) String notes) {
        DocumentRecord verifiedDocument = documentService.verifyDocument(id, verifiedByUserId, notes);
        return ResponseEntity.ok(verifiedDocument);
    }

    @PutMapping("/{id}/unverify")
    public ResponseEntity<DocumentRecord> unverifyDocument(@PathVariable String id) {
        DocumentRecord unverifiedDocument = documentService.unverifyDocument(id);
        return ResponseEntity.ok(unverifiedDocument);
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restoreDocument(@PathVariable String id) {
        documentService.restore(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentRecord>> searchDocuments(
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) Integer documentTypeId,
            @RequestParam(required = false) Boolean isVerified) {
        List<DocumentRecord> documents = documentService.searchDocuments(documentNumber, documentTypeId, isVerified);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Object> getUserDocumentStats(@PathVariable String userId) {
        Object stats = documentService.getUserDocumentStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/verifier/{verifierId}")
    public ResponseEntity<Object> getVerifierStats(@PathVariable String verifierId) {
        Object stats = documentService.getVerifierStats(verifierId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/check-expiry/{id}")
    public ResponseEntity<Boolean> checkDocumentExpiry(@PathVariable String id) {
        boolean isExpired = documentService.isDocumentExpired(id);
        return ResponseEntity.ok(isExpired);
    }

    @GetMapping("/validate/{id}")
    public ResponseEntity<Boolean> validateDocument(@PathVariable String id) {
        boolean isValid = documentService.validateDocument(id);
        return ResponseEntity.ok(isValid);
    }

    @PutMapping("/{id}/update-expiry")
    public ResponseEntity<DocumentRecord> updateExpiryDate(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newExpiryDate) {
        DocumentRecord updatedDocument = documentService.updateExpiryDate(id, newExpiryDate);
        return ResponseEntity.ok(updatedDocument);
    }
}