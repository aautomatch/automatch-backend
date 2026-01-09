package com.automatch.portal.controller;

import com.automatch.portal.records.StudentFavoriteRecord;
import com.automatch.portal.service.StudentFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protected/student-favorite")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StudentFavoriteController {
    private final StudentFavoriteService studentFavoriteService;

    @PostMapping
    public ResponseEntity<StudentFavoriteRecord> addFavorite(@RequestBody StudentFavoriteRecord favoriteRecord) {
        StudentFavoriteRecord createdFavorite = studentFavoriteService.save(favoriteRecord);
        return ResponseEntity.ok(createdFavorite);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentFavoriteRecord> getFavoriteById(@PathVariable String id) {
        StudentFavoriteRecord favorite = studentFavoriteService.getById(id);
        return ResponseEntity.ok(favorite);
    }

    @GetMapping
    public ResponseEntity<List<StudentFavoriteRecord>> getAllFavorites() {
        List<StudentFavoriteRecord> favorites = studentFavoriteService.getAll();
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentFavoriteRecord>> getFavoritesByStudent(@PathVariable String studentId) {
        List<StudentFavoriteRecord> favorites = studentFavoriteService.getByStudent(studentId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<StudentFavoriteRecord>> getFavoritesByInstructor(@PathVariable String instructorId) {
        List<StudentFavoriteRecord> favorites = studentFavoriteService.getByInstructor(instructorId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkIfFavorite(
            @RequestParam String studentId,
            @RequestParam String instructorId) {
        boolean isFavorite = studentFavoriteService.isFavorite(studentId, instructorId);
        return ResponseEntity.ok(isFavorite);
    }

    @GetMapping("/student/{studentId}/count")
    public ResponseEntity<Integer> countFavoritesByStudent(@PathVariable String studentId) {
        int count = studentFavoriteService.countByStudent(studentId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/instructor/{instructorId}/count")
    public ResponseEntity<Integer> countFavoritesByInstructor(@PathVariable String instructorId) {
        int count = studentFavoriteService.countByInstructor(instructorId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/instructor/{instructorId}/popularity")
    public ResponseEntity<Integer> getInstructorPopularity(@PathVariable String instructorId) {
        int popularity = studentFavoriteService.getInstructorPopularity(instructorId);
        return ResponseEntity.ok(popularity);
    }

    @GetMapping("/student/{studentId}/recent")
    public ResponseEntity<List<StudentFavoriteRecord>> getRecentFavorites(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "10") int limit) {
        List<StudentFavoriteRecord> favorites = studentFavoriteService.getRecentByStudent(studentId, limit);
        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String id) {
        studentFavoriteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFavoriteByStudentAndInstructor(
            @RequestParam String studentId,
            @RequestParam String instructorId) {
        studentFavoriteService.deleteByStudentAndInstructor(studentId, instructorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/student/{studentId}")
    public ResponseEntity<Void> removeAllByStudent(@PathVariable String studentId) {
        studentFavoriteService.deleteAllByStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top-instructors")
    public ResponseEntity<List<String>> getTopFavoriteInstructors(
            @RequestParam(defaultValue = "10") int limit) {
        List<String> instructorIds = studentFavoriteService.getTopFavoriteInstructors(limit);
        return ResponseEntity.ok(instructorIds);
    }
}