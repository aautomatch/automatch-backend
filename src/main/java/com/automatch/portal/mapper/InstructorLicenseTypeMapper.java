package com.automatch.portal.mapper;

import com.automatch.portal.model.InstructorLicenseTypeModel;
import com.automatch.portal.records.InstructorLicenseTypeRecord;

import java.util.UUID;

public class InstructorLicenseTypeMapper {

    public static InstructorLicenseTypeRecord toRecord(InstructorLicenseTypeModel model) {
        if (model == null) return null;
        return new InstructorLicenseTypeRecord(
                model.getId().toString(),
                InstructorMapper.toRecord(model.getInstructor()),
                ClassifierMapper.toRecord(model.getLicenseType()),
                model.getCreatedAt()
        );
    }

    public static InstructorLicenseTypeModel fromRecord(InstructorLicenseTypeRecord record) {
        if (record == null) return null;
        InstructorLicenseTypeModel model = new InstructorLicenseTypeModel();
        model.setId(UUID.fromString(record.id()));
        model.setInstructor(InstructorMapper.fromRecord(record.instructor()));
        model.setLicenseType(ClassifierMapper.fromRecord(record.licenseType()));
        model.setCreatedAt(record.createdAt());
        return model;
    }
}
