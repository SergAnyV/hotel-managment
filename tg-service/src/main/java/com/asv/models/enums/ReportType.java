package com.asv.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportType {
    ISSUE("Проблема или дефект"),
    WORK("Отчет о выполненной работе");
    private final String description;
}
