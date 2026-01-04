package com.automatch.portal.mapper;

import com.automatch.portal.model.PaymentModel;
import com.automatch.portal.records.PaymentRecord;

import java.util.UUID;

public class PaymentMapper {

    public static PaymentRecord toRecord(PaymentModel model) {
        if (model == null) return null;
        return new PaymentRecord(
                model.getId().toString(),
                LessonMapper.toRecord(model.getLesson()),
                model.getAmount(),
                ClassifierMapper.toRecord(model.getStatus()),
                ClassifierMapper.toRecord(model.getPaymentMethod()),
                model.getTransactionId(),
                model.getPaidAt(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getDeletedAt()
        );
    }

    public static PaymentModel fromRecord(PaymentRecord record) {
        if (record == null) return null;
        PaymentModel model = new PaymentModel();
        model.setId(UUID.fromString(record.id()));
        model.setLesson(LessonMapper.fromRecord(record.lesson()));
        model.setAmount(record.amount());
        model.setStatus(ClassifierMapper.fromRecord(record.status()));
        model.setPaymentMethod(ClassifierMapper.fromRecord(record.paymentMethod()));
        model.setTransactionId(record.transactionId());
        model.setPaidAt(record.paidAt());
        model.setCreatedAt(record.createdAt());
        model.setUpdatedAt(record.updatedAt());
        model.setDeletedAt(record.deletedAt());
        return model;
    }
}
