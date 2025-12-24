package com.asv.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeOfPromoCode {
    FIXED("фиксированая скидка "),
    PERCENT("скидка в виде процента от стоимости");

    private final String description;

}
