package com.automatch.portal.mapper;

import com.automatch.portal.model.ReviewModel;
import com.automatch.portal.records.ReviewRecord;

import java.util.UUID;

public class ReviewMapper {

    public static ReviewRecord toRecord(ReviewModel model) {
        if (model == null) return null;
        System.out.println("teste");
        return new ReviewRecord(
                model.getId().toString(),
                LessonMapper.toRecord(model.getLesson()),
                model.getRating(),
                model.getComment(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
        );
    }

    public static ReviewModel fromRecord(ReviewRecord record) {
        if (record == null) return null;
        ReviewModel model = new ReviewModel();
        model.setId(UUID.fromString(record.id()));
        model.setLesson(LessonMapper.fromRecord(record.lesson()));
        model.setRating(record.rating());
        model.setComment(record.comment());
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());
        return model;
    }
}
