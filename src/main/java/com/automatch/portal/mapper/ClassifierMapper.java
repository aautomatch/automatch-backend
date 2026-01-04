package com.automatch.portal.mapper;

import com.automatch.portal.model.ClassifierModel;
import com.automatch.portal.records.ClassifierRecord;

public class ClassifierMapper {

    public static ClassifierRecord toRecord(ClassifierModel model) {
        if (model == null) return null;
        return new ClassifierRecord(
                model.getId(),
                model.getType(),
                model.getValue(),
                model.getDescription()
        );
    }

    public static ClassifierModel fromRecord(ClassifierRecord record) {
        if (record == null) return null;
        return new ClassifierModel(
                record.id(),
                record.type(),
                record.value(),
                record.description()
        );
    }
}
