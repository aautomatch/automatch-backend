package com.automatch.portal.mapper;

import com.automatch.portal.model.ReviewModel;
import com.automatch.portal.records.ReviewRecord;

import java.util.UUID;

public class ReviewMapper {

    public static ReviewRecord toRecord(ReviewModel model) {
        if (model == null) return null;

        return new ReviewRecord(
                model.getId() != null ? model.getId().toString() : null,
                model.getLessonId() != null ? model.getLessonId().toString() : null,
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

        if (record.id() != null) {
            model.setId(UUID.fromString(record.id()));
        }

        if (record.lessonId() != null) {
            model.setLessonId(UUID.fromString(record.lessonId()));
        }

        model.setRating(record.rating());
        model.setComment(record.comment());
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());

        return model;
    }
}