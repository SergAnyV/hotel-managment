package com.asv.enums;

/**
 * Типы активных процессов пользователя.
 * Используется для определения, какой сценарий сейчас выполняется.
 */
public enum ProcessType {
    NONE,
    USER_REGISTRATION,
    BOOKING,
    SIGN_IN,
    VIEW_BOOKING,
    DELETE_BOOKING,

    REQUEST_INFO,

    GET_ROOM_INFO,
    GET_SERVICE_INFO,
    GET_FREE_ROOMS
}
