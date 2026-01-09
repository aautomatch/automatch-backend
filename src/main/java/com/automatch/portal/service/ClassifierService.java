package com.automatch.portal.service;

import com.automatch.portal.dao.ClassifierDAO;
import com.automatch.portal.mapper.ClassifierMapper;
import com.automatch.portal.model.ClassifierModel;
import com.automatch.portal.records.ClassifierRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassifierService {

    private final ClassifierDAO classifierDAO;

    @Transactional
    public ClassifierRecord save(ClassifierRecord classifierRecord) {
        validateClassifierRecord(classifierRecord);

        ClassifierModel classifierModel = ClassifierMapper.fromRecord(classifierRecord);

        if (classifierModel.getId() == null) {
            return createClassifier(classifierModel);
        } else {
            return updateClassifier(classifierRecord.id(), classifierRecord);
        }
    }

    private ClassifierRecord createClassifier(ClassifierModel classifierModel) {
        // Verificar se já existe um classificador com o mesmo tipo e valor
        if (classifierDAO.existsByTypeAndValue(classifierModel.getType(), classifierModel.getValue())) {
            throw new IllegalArgumentException("Classifier already exists with type: " +
                    classifierModel.getType() + " and value: " + classifierModel.getValue());
        }

        ClassifierModel savedModel = classifierDAO.save(classifierModel);
        return ClassifierMapper.toRecord(savedModel);
    }

    public ClassifierRecord getById(Integer id) {
        return classifierDAO.findById(id)
                .map(ClassifierMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException("Classifier not found with ID: " + id));
    }

    public List<ClassifierRecord> getAll() {
        return classifierDAO.findAll().stream()
                .map(ClassifierMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<ClassifierRecord> getByType(String type) {
        return classifierDAO.findByType(type).stream()
                .map(ClassifierMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<ClassifierRecord> getByValue(String value) {
        return classifierDAO.findByValue(value).stream()
                .map(ClassifierMapper::toRecord)
                .collect(Collectors.toList());
    }

    public ClassifierRecord getByTypeAndValue(String type, String value) {
        return classifierDAO.findByTypeAndValue(type, value)
                .map(ClassifierMapper::toRecord)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Classifier not found with type: " + type + " and value: " + value));
    }

    @Transactional
    public void delete(Integer id) {
        ClassifierModel classifier = classifierDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Classifier not found with ID: " + id));

        boolean deleted = classifierDAO.delete(id);
        if (!deleted) {
            throw new RuntimeException("Failed to delete classifier with ID: " + id);
        }
    }

    @Transactional
    public ClassifierRecord updateClassifier(Integer id, ClassifierRecord classifierRecord) {
        validateClassifierRecord(classifierRecord);

        ClassifierModel existingClassifier = classifierDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Classifier not found with ID: " + id));

        // Verificar se o novo tipo/valor já existe (exceto para este classificador)
        if ((!existingClassifier.getType().equals(classifierRecord.type()) ||
                !existingClassifier.getValue().equals(classifierRecord.value())) &&
                classifierDAO.existsByTypeAndValue(classifierRecord.type(), classifierRecord.value())) {
            throw new IllegalArgumentException("Classifier already exists with type: " +
                    classifierRecord.type() + " and value: " + classifierRecord.value());
        }

        ClassifierModel updatedModel = ClassifierMapper.fromRecord(classifierRecord);
        updatedModel.setId(id);

        ClassifierModel savedModel = classifierDAO.save(updatedModel);
        return ClassifierMapper.toRecord(savedModel);
    }

    public List<ClassifierRecord> searchClassifiers(String type, String value, String description) {
        return classifierDAO.search(type, value, description).stream()
                .map(ClassifierMapper::toRecord)
                .collect(Collectors.toList());
    }

    public List<String> getAllTypes() {
        return classifierDAO.findAllTypes();
    }

    public boolean existsById(Integer id) {
        return classifierDAO.existsById(id);
    }

    public int countByType(String type) {
        return classifierDAO.countByType(type);
    }

    public int countAll() {
        return classifierDAO.countAll();
    }

    private void validateClassifierRecord(ClassifierRecord classifierRecord) {
        if (classifierRecord == null) {
            throw new IllegalArgumentException("Classifier record cannot be null");
        }

        if (classifierRecord.type() == null || classifierRecord.type().trim().isEmpty()) {
            throw new IllegalArgumentException("Type is required");
        }

        if (classifierRecord.value() == null || classifierRecord.value().trim().isEmpty()) {
            throw new IllegalArgumentException("Value is required");
        }
    }
}