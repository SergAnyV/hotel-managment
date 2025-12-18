package com.asv.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
    MANAGER("Менеджер"),
    ADMIN("Администратор"),
    CLIENT("Посетитель"),
    STAFF("Персонал");
    private final String description;
}
