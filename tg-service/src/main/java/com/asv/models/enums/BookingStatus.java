package com.asv.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BookingStatus {
    REQUEST(" запрос на подтверждение брони"),
    CONFIRMED(" бронирование подтвреждено"),
    CANCELLED("бронирование отменено");


    private final String description;
}
