package com.automatch.portal.mapper;

import com.automatch.portal.model.ClassifierModel;
import com.automatch.portal.records.ClassifierRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static RowMapper<ClassifierModel> getRowMapper() {
        return new RowMapper<ClassifierModel>() {
            @Override
            public ClassifierModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                ClassifierModel classifier = new ClassifierModel();
                classifier.setId(rs.getInt("id"));
                classifier.setType(rs.getString("type"));
                classifier.setValue(rs.getString("value"));
                classifier.setDescription(rs.getString("description"));
                return classifier;
            }
        };
    }
}