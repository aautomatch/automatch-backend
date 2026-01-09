package com.automatch.portal.controller;

import com.automatch.portal.records.ClassifierRecord;
import com.automatch.portal.service.ClassifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protected/classifier")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClassifierController {
    private final ClassifierService classifierService;

    @PostMapping
    public ResponseEntity<ClassifierRecord> createClassifier(@RequestBody ClassifierRecord classifierRecord) {
        ClassifierRecord createdClassifier = classifierService.save(classifierRecord);
        return ResponseEntity.ok(createdClassifier);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassifierRecord> getClassifierById(@PathVariable Integer id) {
        ClassifierRecord classifier = classifierService.getById(id);
        return ResponseEntity.ok(classifier);
    }

    @GetMapping
    public ResponseEntity<List<ClassifierRecord>> getAllClassifiers() {
        List<ClassifierRecord> classifiers = classifierService.getAll();
        return ResponseEntity.ok(classifiers);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ClassifierRecord>> getClassifiersByType(@PathVariable String type) {
        List<ClassifierRecord> classifiers = classifierService.getByType(type);
        return ResponseEntity.ok(classifiers);
    }

    @GetMapping("/value/{value}")
    public ResponseEntity<List<ClassifierRecord>> getClassifiersByValue(@PathVariable String value) {
        List<ClassifierRecord> classifiers = classifierService.getByValue(value);
        return ResponseEntity.ok(classifiers);
    }

    @GetMapping("/type/{type}/value/{value}")
    public ResponseEntity<ClassifierRecord> getClassifierByTypeAndValue(
            @PathVariable String type,
            @PathVariable String value) {
        ClassifierRecord classifier = classifierService.getByTypeAndValue(type, value);
        return ResponseEntity.ok(classifier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassifierRecord> updateClassifier(
            @PathVariable Integer id,
            @RequestBody ClassifierRecord classifierRecord) {
        ClassifierRecord updatedClassifier = classifierService.updateClassifier(id, classifierRecord);
        return ResponseEntity.ok(updatedClassifier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassifier(@PathVariable Integer id) {
        classifierService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClassifierRecord>> searchClassifiers(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String value,
            @RequestParam(required = false) String description) {
        List<ClassifierRecord> classifiers = classifierService.searchClassifiers(type, value, description);
        return ResponseEntity.ok(classifiers);
    }

    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllTypes() {
        List<String> types = classifierService.getAllTypes();
        return ResponseEntity.ok(types);
    }
}