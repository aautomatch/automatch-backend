package com.automatch.portal.controller;

import com.automatch.portal.records.LessonRecord;
import com.automatch.portal.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/protected/lesson")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<LessonRecord> createLesson(@RequestBody LessonRecord lessonRecord) {
        LessonRecord createdLesson = lessonService.save(lessonRecord);
        return ResponseEntity.ok(createdLesson);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonRecord> getLessonById(@PathVariable String id) {
        LessonRecord lesson = lessonService.getById(id);
        return ResponseEntity.ok(lesson);
    }

    @GetMapping
    public ResponseEntity<List<LessonRecord>> getAllLessons() {
        List<LessonRecord> lessons = lessonService.getAll();
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<LessonRecord>> getLessonsByInstructor(@PathVariable String instructorId) {
        List<LessonRecord> lessons = lessonService.getByInstructor(instructorId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<LessonRecord>> getLessonsByStudent(@PathVariable String studentId) {
        List<LessonRecord> lessons = lessonService.getByStudent(studentId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<LessonRecord>> getLessonsByVehicle(@PathVariable String vehicleId) {
        List<LessonRecord> lessons = lessonService.getByVehicle(vehicleId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<LessonRecord>> getLessonsByStatus(@PathVariable Integer statusId) {
        List<LessonRecord> lessons = lessonService.getByStatus(statusId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/payment-status/{paymentStatusId}")
    public ResponseEntity<List<LessonRecord>> getLessonsByPaymentStatus(@PathVariable Integer paymentStatusId) {
        List<LessonRecord> lessons = lessonService.getByPaymentStatus(paymentStatusId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/scheduled")
    public ResponseEntity<List<LessonRecord>> getUpcomingLessons() {
        List<LessonRecord> lessons = lessonService.getUpcomingLessons();
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<LessonRecord>> getCompletedLessons() {
        List<LessonRecord> lessons = lessonService.getCompletedLessons();
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<LessonRecord>> getLessonsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LessonRecord> lessons = lessonService.getByDate(date);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LessonRecord>> getLessonsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<LessonRecord> lessons = lessonService.getByDateRange(startDate, endDate);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/instructor/{instructorId}/date/{date}")
    public ResponseEntity<List<LessonRecord>> getInstructorLessonsByDate(
            @PathVariable String instructorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LessonRecord> lessons = lessonService.getInstructorLessonsByDate(instructorId, date);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/student/{studentId}/date/{date}")
    public ResponseEntity<List<LessonRecord>> getStudentLessonsByDate(
            @PathVariable String studentId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<LessonRecord> lessons = lessonService.getStudentLessonsByDate(studentId, date);
        return ResponseEntity.ok(lessons);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonRecord> updateLesson(@PathVariable String id, @RequestBody LessonRecord lessonRecord) {
        LessonRecord updatedLesson = lessonService.updateLesson(id, lessonRecord);
        return ResponseEntity.ok(updatedLesson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable String id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<LessonRecord> completeLesson(@PathVariable String id) {
        LessonRecord completedLesson = lessonService.completeLesson(id);
        return ResponseEntity.ok(completedLesson);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<LessonRecord> cancelLesson(@PathVariable String id) {
        LessonRecord cancelledLesson = lessonService.cancelLesson(id);
        return ResponseEntity.ok(cancelledLesson);
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<LessonRecord> rescheduleLesson(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime) {
        LessonRecord rescheduledLesson = lessonService.rescheduleLesson(id, newDateTime);
        return ResponseEntity.ok(rescheduledLesson);
    }

    @PutMapping("/{id}/update-payment-status")
    public ResponseEntity<LessonRecord> updatePaymentStatus(
            @PathVariable String id,
            @RequestParam Integer paymentStatusId) {
        LessonRecord updatedLesson = lessonService.updatePaymentStatus(id, paymentStatusId);
        return ResponseEntity.ok(updatedLesson);
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<LessonRecord> updateStatus(
            @PathVariable String id,
            @RequestParam Integer statusId) {
        LessonRecord updatedLesson = lessonService.updateStatus(id, statusId);
        return ResponseEntity.ok(updatedLesson);
    }

    @GetMapping("/stats/instructor/{instructorId}")
    public ResponseEntity<Object> getInstructorStats(@PathVariable String instructorId) {
        Object stats = lessonService.getInstructorStats(instructorId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/student/{studentId}")
    public ResponseEntity<Object> getStudentStats(@PathVariable String studentId) {
        Object stats = lessonService.getStudentStats(studentId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue/instructor/{instructorId}")
    public ResponseEntity<BigDecimal> getInstructorRevenue(@PathVariable String instructorId) {
        BigDecimal revenue = lessonService.getInstructorRevenue(instructorId);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/pending-payment/instructor/{instructorId}")
    public ResponseEntity<List<LessonRecord>> getInstructorPendingPayment(@PathVariable String instructorId) {
        List<LessonRecord> lessons = lessonService.getInstructorPendingPayment(instructorId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/conflict-check")
    public ResponseEntity<Boolean> checkScheduleConflict(
            @RequestParam String instructorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam Integer durationMinutes) {
        boolean hasConflict = lessonService.checkScheduleConflict(instructorId, startTime, durationMinutes);
        return ResponseEntity.ok(hasConflict);
    }
}