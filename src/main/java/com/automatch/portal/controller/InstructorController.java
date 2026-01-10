package com.automatch.portal.controller;

import com.automatch.portal.records.InstructorRecord;
import com.automatch.portal.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/protected/instructor")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InstructorController {
    private final InstructorService instructorService;

    @PostMapping
    public ResponseEntity<InstructorRecord> createInstructor(@RequestBody InstructorRecord instructorRecord) {
        InstructorRecord createdInstructor = instructorService.save(instructorRecord);
        return ResponseEntity.ok(createdInstructor);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<InstructorRecord> getInstructorById(@PathVariable String userId) {
        InstructorRecord instructor = instructorService.getById(userId);
        return ResponseEntity.ok(instructor);
    }

//    @GetMapping
//    public ResponseEntity<List<InstructorRecord>> getAllInstructors() {
//        List<InstructorRecord> instructors = instructorService.getAll();
//        return ResponseEntity.ok(instructors);
//    }

    @GetMapping("/active")
    public ResponseEntity<List<InstructorRecord>> getActiveInstructors() {
        List<InstructorRecord> instructors = instructorService.getActiveInstructors();
        return ResponseEntity.ok(instructors);
    }

//    @GetMapping("/verified")
//    public ResponseEntity<List<InstructorRecord>> getVerifiedInstructors() {
//        List<InstructorRecord> instructors = instructorService.getVerifiedInstructors();
//        return ResponseEntity.ok(instructors);
//    }

//    @GetMapping("/search")
//    public ResponseEntity<List<InstructorRecord>> searchInstructors(
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) Integer minYearsExperience,
//            @RequestParam(required = false) BigDecimal maxHourlyRate,
//            @RequestParam(required = false) BigDecimal minRating) {
//        List<InstructorRecord> instructors = instructorService.searchInstructors(name, minYearsExperience, maxHourlyRate, minRating);
//        return ResponseEntity.ok(instructors);
//    }

//    @GetMapping("/top-rated")
//    public ResponseEntity<List<InstructorRecord>> getTopRatedInstructors(
//            @RequestParam(defaultValue = "10") int limit) {
//        List<InstructorRecord> instructors = instructorService.getTopRatedInstructors(limit);
//        return ResponseEntity.ok(instructors);
//    }

//    @GetMapping("/available-now")
//    public ResponseEntity<List<InstructorRecord>> getAvailableInstructorsNow() {
//        List<InstructorRecord> instructors = instructorService.getAvailableInstructorsNow();
//        return ResponseEntity.ok(instructors);
//    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<Object> getInstructorStats(@PathVariable String userId) {
        Object stats = instructorService.getInstructorStats(userId);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<InstructorRecord> updateInstructor(
            @PathVariable String userId,
            @RequestBody InstructorRecord instructorRecord) {
        InstructorRecord updatedInstructor = instructorService.updateInstructor(userId, instructorRecord);
        return ResponseEntity.ok(updatedInstructor);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteInstructor(@PathVariable String userId) {
        instructorService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/verify")
    public ResponseEntity<InstructorRecord> verifyInstructor(@PathVariable String userId) {
        InstructorRecord verifiedInstructor = instructorService.verifyInstructor(userId);
        return ResponseEntity.ok(verifiedInstructor);
    }

    @PutMapping("/{userId}/unverify")
    public ResponseEntity<InstructorRecord> unverifyInstructor(@PathVariable String userId) {
        InstructorRecord unverifiedInstructor = instructorService.unverifyInstructor(userId);
        return ResponseEntity.ok(unverifiedInstructor);
    }

    @PutMapping("/{userId}/update-rating")
    public ResponseEntity<InstructorRecord> updateInstructorRating(
            @PathVariable String userId,
            @RequestParam BigDecimal newRating) {
        InstructorRecord updatedInstructor = instructorService.updateRating(userId, newRating);
        return ResponseEntity.ok(updatedInstructor);
    }

    @PutMapping("/{userId}/add-review")
    public ResponseEntity<InstructorRecord> addReviewToInstructor(
            @PathVariable String userId,
            @RequestParam Integer rating) {
        InstructorRecord updatedInstructor = instructorService.addReview(userId, rating);
        return ResponseEntity.ok(updatedInstructor);
    }

    @GetMapping("/license-types/{userId}")
    public ResponseEntity<List<Integer>> getInstructorLicenseTypes(@PathVariable String userId) {
        List<Integer> licenseTypes = instructorService.getInstructorLicenseTypes(userId);
        return ResponseEntity.ok(licenseTypes);
    }

    @PostMapping("/license-types/{userId}")
    public ResponseEntity<Void> addLicenseTypeToInstructor(
            @PathVariable String userId,
            @RequestParam Integer licenseTypeId) {
        instructorService.addLicenseType(userId, licenseTypeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/license-types/{userId}/{licenseTypeId}")
    public ResponseEntity<Void> removeLicenseTypeFromInstructor(
            @PathVariable String userId,
            @PathVariable Integer licenseTypeId) {
        instructorService.removeLicenseType(userId, licenseTypeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/vehicles")
    public ResponseEntity<List<Map<String, Object>>> getInstructorVehicles(@PathVariable String userId) {
        List<Map<String, Object>> vehicles = instructorService.getInstructorVehicles(userId);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{userId}/schedule")
    public ResponseEntity<List<Map<String, Object>>> getInstructorSchedule(@PathVariable String userId) {
        List<Map<String, Object>> schedule = instructorService.getInstructorSchedule(userId);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/{userId}/reviews")
    public ResponseEntity<List<Map<String, Object>>> getInstructorReviews(@PathVariable String userId) {
        List<Map<String, Object>> reviews = instructorService.getInstructorReviews(userId);
        return ResponseEntity.ok(reviews);
    }

//    @GetMapping("/count")
//    public ResponseEntity<Integer> countInstructors() {
//        int count = instructorService.countInstructors();
//        return ResponseEntity.ok(count);
//    }
//
//    @GetMapping("/count/verified")
//    public ResponseEntity<Integer> countVerifiedInstructors() {
//        int count = instructorService.countVerifiedInstructors();
//        return ResponseEntity.ok(count);
//    }
//
//    @GetMapping("/hourly-rate/stats")
//    public ResponseEntity<Object> getHourlyRateStats() {
//        Object stats = instructorService.getHourlyRateStats();
//        return ResponseEntity.ok(stats);
//    }
}