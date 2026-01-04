package com.automatch.portal.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassifierModel {
    private Integer id;
    private String type;
    private String value;
    private String description;
}
