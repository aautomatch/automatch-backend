package com.automatch.portal.controller;

import com.automatch.portal.records.InstructorAvailabilityRecord;
import com.automatch.portal.service.InstructorAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/protected/instructor-availability")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InstructorAvailabilityController {
    private final InstructorAvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<InstructorAvailabilityRecord> createAvailability(@RequestBody InstructorAvailabilityRecord availabilityRecord) {
        InstructorAvailabilityRecord createdAvailability = availabilityService.save(availabilityRecord);
        return ResponseEntity.ok(createdAvailability);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<InstructorAvailabilityRecord>> createBatchAvailabilities(
            @RequestBody List<InstructorAvailabilityRecord> availabilityRecords) {
        List<InstructorAvailabilityRecord> createdAvailabilities = availabilityService.saveBatch(availabilityRecords);
        return ResponseEntity.ok(createdAvailabilities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorAvailabilityRecord> getAvailabilityById(@PathVariable String id) {
        InstructorAvailabilityRecord availability = availabilityService.getById(id);
        return ResponseEntity.ok(availability);
    }

    @GetMapping
    public ResponseEntity<List<InstructorAvailabilityRecord>> getAllAvailabilities() {
        List<InstructorAvailabilityRecord> availabilities = availabilityService.getAll();
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<InstructorAvailabilityRecord>> getAvailabilitiesByInstructor(@PathVariable String instructorId) {
        List<InstructorAvailabilityRecord> availabilities = availabilityService.getByInstructor(instructorId);
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/instructor/{instructorId}/day/{dayOfWeek}")
    public ResponseEntity<List<InstructorAvailabilityRecord>> getAvailabilitiesByInstructorAndDay(
            @PathVariable String instructorId,
            @PathVariable Integer dayOfWeek) {
        List<InstructorAvailabilityRecord> availabilities = availabilityService.getByInstructorAndDay(instructorId, dayOfWeek);
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/day/{dayOfWeek}")
    public ResponseEntity<List<InstructorAvailabilityRecord>> getAvailabilitiesByDay(@PathVariable Integer dayOfWeek) {
        List<InstructorAvailabilityRecord> availabilities = availabilityService.getByDay(dayOfWeek);
        return ResponseEntity.ok(availabilities);
    }

    @GetMapping("/instructor/{instructorId}/weekly")
    public ResponseEntity<List<InstructorAvailabilityRecord>> getWeeklySchedule(@PathVariable String instructorId) {
        List<InstructorAvailabilityRecord> schedule = availabilityService.getWeeklySchedule(instructorId);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkInstructorAvailability(
            @RequestParam String instructorId,
            @RequestParam Integer dayOfWeek,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime) {
        boolean isAvailable = availabilityService.checkAvailability(instructorId, dayOfWeek, startTime, endTime);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/find-available-instructors")
    public ResponseEntity<List<String>> findAvailableInstructors(
            @RequestParam Integer dayOfWeek,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime) {
        List<String> instructorIds = availabilityService.findAvailableInstructors(dayOfWeek, startTime, endTime);
        return ResponseEntity.ok(instructorIds);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorAvailabilityRecord> updateAvailability(
            @PathVariable String id,
            @RequestBody InstructorAvailabilityRecord availabilityRecord) {
        InstructorAvailabilityRecord updatedAvailability = availabilityService.updateAvailability(id, availabilityRecord);
        return ResponseEntity.ok(updatedAvailability);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable String id) {
        availabilityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/instructor/{instructorId}")
    public ResponseEntity<Void> deleteAllByInstructor(@PathVariable String instructorId) {
        availabilityService.deleteAllByInstructor(instructorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/instructor/{instructorId}/day/{dayOfWeek}")
    public ResponseEntity<Void> deleteByInstructorAndDay(
            @PathVariable String instructorId,
            @PathVariable Integer dayOfWeek) {
        availabilityService.deleteByInstructorAndDay(instructorId, dayOfWeek);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Void> restoreAvailability(@PathVariable String id) {
        availabilityService.restore(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/instructor/{instructorId}/next-available")
    public ResponseEntity<InstructorAvailabilityRecord> getNextAvailableSlot(
            @PathVariable String instructorId,
            @RequestParam(required = false) Integer dayOfWeek) {
        InstructorAvailabilityRecord nextSlot = availabilityService.getNextAvailableSlot(instructorId, dayOfWeek);
        return ResponseEntity.ok(nextSlot);
    }

    @GetMapping("/instructor/{instructorId}/overlap-check")
    public ResponseEntity<Boolean> checkForOverlap(
            @PathVariable String instructorId,
            @RequestParam Integer dayOfWeek,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            @RequestParam(required = false) String excludeId) {
        boolean hasOverlap = availabilityService.checkForOverlap(instructorId, dayOfWeek, startTime, endTime, excludeId);
        return ResponseEntity.ok(hasOverlap);
    }
}