package com.asv.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportStatus {
    SUBMITTED("подтвержден"),
    APPROVED("одобрен"),
    REJECTED("отклонен"),
    FIXED("исправлен"),
    CANCELLED("отменен"),
    ISSUED("выпущен");

    private final String description;
}
