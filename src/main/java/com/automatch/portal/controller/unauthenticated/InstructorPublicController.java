package com.automatch.portal.controller.unauthenticated;

import com.automatch.portal.service.unauthenticated.InstructorPublicService;
import com.automatch.portal.records.InstructorPublicRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/public/instructor")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InstructorPublicController {

    private final InstructorPublicService instructorPublicService;

    @GetMapping
    public ResponseEntity<List<InstructorPublicRecord>> getAllInstructors() {
        List<InstructorPublicRecord> instructors = instructorPublicService.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/search")
    public ResponseEntity<List<InstructorPublicRecord>> searchInstructors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minYearsExperience,
            @RequestParam(required = false) BigDecimal maxHourlyRate,
            @RequestParam(required = false) BigDecimal minRating,
            @RequestParam(required = false) String city) { // Novo parâmetro

        List<InstructorPublicRecord> instructors = instructorPublicService.searchInstructors(
                name, minYearsExperience, maxHourlyRate, minRating, city);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/verified")
    public ResponseEntity<List<InstructorPublicRecord>> getVerifiedInstructors() {
        List<InstructorPublicRecord> instructors = instructorPublicService.getVerifiedInstructors();
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<InstructorPublicRecord>> getTopRatedInstructors(
            @RequestParam(defaultValue = "10") int limit) {
        List<InstructorPublicRecord> instructors = instructorPublicService.getTopRatedInstructors(limit);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/available-now")
    public ResponseEntity<List<InstructorPublicRecord>> getAvailableInstructorsNow() {
        List<InstructorPublicRecord> instructors = instructorPublicService.getAvailableInstructorsNow();
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/by-hourly-rate")
    public ResponseEntity<List<InstructorPublicRecord>> getInstructorsByHourlyRateRange(
            @RequestParam(required = false) BigDecimal minRate,
            @RequestParam(required = false) BigDecimal maxRate,
            @RequestParam(required = false) String city) { // Novo parâmetro opcional

        List<InstructorPublicRecord> instructors = instructorPublicService.getInstructorsByHourlyRateRange(minRate, maxRate, city);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/by-experience")
    public ResponseEntity<List<InstructorPublicRecord>> getInstructorsByExperienceRange(
            @RequestParam(required = false) Integer minYears,
            @RequestParam(required = false) Integer maxYears,
            @RequestParam(required = false) String city) { // Novo parâmetro opcional

        List<InstructorPublicRecord> instructors = instructorPublicService.getInstructorsByExperienceRange(minYears, maxYears, city);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/by-city/{city}")
    public ResponseEntity<List<InstructorPublicRecord>> getInstructorsByCity(@PathVariable String city) {
        List<InstructorPublicRecord> instructors = instructorPublicService.getInstructorsByCity(city);
        return ResponseEntity.ok(instructors);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        List<String> cities = instructorPublicService.getAllCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> countInstructors() {
        int count = instructorPublicService.countInstructors();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/verified")
    public ResponseEntity<Integer> countVerifiedInstructors() {
        int count = instructorPublicService.countVerifiedInstructors();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stats/hourly-rate")
    public ResponseEntity<Object> getHourlyRateStats() {
        Object stats = instructorPublicService.getHourlyRateStats();
        return ResponseEntity.ok(stats);
    }
}