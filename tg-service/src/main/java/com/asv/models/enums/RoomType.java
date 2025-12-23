package com.asv.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoomType {
    ECONOM("обычный номер чтобы переночевать"),
    STANDART("улучшенный номер с небольшими улучшениями"),
    LUXE("номер повышенного комфорта "),
    DELUXE("номер- комфорт уровня 'все сделаем за вас не переживайте', вы наше все  ");
    private final String description;
}
