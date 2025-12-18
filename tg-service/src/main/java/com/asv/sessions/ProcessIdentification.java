package com.asv.sessions;

import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;

import java.util.*;

/**
 * Класс для управления логикой пошаговых процессов пользователя.
 * Содержит карту соответствия между типами процессов ({@link ProcessType})
 * и последовательностями их шагов ({@link ProcessState}).
 * Предоставляет методы для навигации по шагам, проверки валидности состояний
 * и получения метаданных о процессах (номер шага, общее количество шагов и т.д.).
 */
public class ProcessIdentification {
    /**
     * Карта, сопоставляющая каждый тип процесса со списком его последовательных шагов.
     */
    private static final Map<ProcessType, List<ProcessState>> PROCESS_STEPS = new HashMap<>();

    static {

        PROCESS_STEPS.put(ProcessType.SIGN_IN, Arrays.asList(
                ProcessState.SIGN_IN_ENTERING_LOGIN,
                ProcessState.SIGN_IN_ENTERING_PASSWORD
        ));

        PROCESS_STEPS.put(ProcessType.USER_REGISTRATION, Arrays.asList(
                ProcessState.REGISTRATION_ENTERING_NICK_NAME,
                ProcessState.REGISTRATION_ENTERING_NAME,
                ProcessState.REGISTRATION_ENTERING_FATHERS_NAME,
                ProcessState.REGISTRATION_ENTERING_FAMILY_NAME,
                ProcessState.REGISTRATION_ENTERING_EMAIL,
                ProcessState.REGISTRATION_ENTERING_PHONE_NUMBER,
                ProcessState.REGISTRATION_ENTERING_PASSWORD
        ));
        PROCESS_STEPS.put(ProcessType.BOOKING, Arrays.asList(
                ProcessState.BOOKING_ENTERING_CHECK_IN_DATE,
                ProcessState.BOOKING_ENTERING_CHECK_OUT_DATE,
                ProcessState.BOOKING_ENTERING_ROOM_NUMBER,
                ProcessState.BOOKING_ENTERING_QUANTITY_PERSONS,
                ProcessState.BOOKING_ENTERING_GUEST_NAME,
                ProcessState.BOOKING_ENTERING_GUEST_SURNAME,
                ProcessState.BOOKING_ENTERING_GUEST_AGE,
                ProcessState.BOOKING_ENTERING_GUEST_DOCUMENT,
                ProcessState.BOOKING_ENTERING_PROMO_CODE,
                ProcessState.BOOKING_ENTERING_SERVICES
        ));

        PROCESS_STEPS.put(ProcessType.VIEW_BOOKING, Arrays.asList(
                ProcessState.VIEW_BOOKING_ENTERING_ID
        ));

        PROCESS_STEPS.put(ProcessType.DELETE_BOOKING, Arrays.asList(
                ProcessState.DELETE_BOOKING_ENTERING_ID
        ));


        PROCESS_STEPS.put(ProcessType.GET_ROOM_INFO, Arrays.asList(
                ProcessState.GET_ROOM_INFO_ENTERING_NUMBER
        ));

        PROCESS_STEPS.put(ProcessType.GET_SERVICE_INFO, Arrays.asList(
                ProcessState.GET_SERVICE_INFO_ENTERING_NAME
        ));

        PROCESS_STEPS.put(ProcessType.GET_FREE_ROOMS, Arrays.asList(
                ProcessState.GET_FREE_ROOMS_ENTERING_CHECK_IN_DATE,
                ProcessState.GET_FREE_ROOMS_ENTERING_CHECK_OUT_DATE
        ));
    }
    /**
     * Проверяет, является ли указанная комбинация типа процесса и состояния допустимой.
     * Состояние {@code NONE} для обоих параметров считается валидным.
     *
     * @param type  тип процесса
     * @param state состояние (шаг) процесса
     * @return {@code true}, если комбинация валидна; {@code false} в противном случае
     */
    public static boolean isValidStep(ProcessType type, ProcessState state) {
        if (type == ProcessType.NONE && state == ProcessState.NONE) {
            return true;
        }

        List<ProcessState> steps = PROCESS_STEPS.get(type);
        return steps != null && steps.contains(state);
    }
    /**
     * Возвращает следующий шаг процесса после указанного текущего шага.
     *
     * @param type    тип процесса
     * @param current текущее состояние (шаг)
     * @return следующее состояние, если оно существует; {@link ProcessState#NONE} в противном случае
     */
    public static ProcessState getNextStep(ProcessType type, ProcessState current) {
        List<ProcessState> steps = PROCESS_STEPS.get(type);
        if (steps == null) return ProcessState.NONE;

        int index = steps.indexOf(current);
        if (index >= 0 && index < steps.size() - 1) {
            return steps.get(index + 1);
        }
        return ProcessState.NONE;
    }
    /**
     * Возвращает первый шаг указанного типа процесса.
     *
     * @param type тип процесса
     * @return первый шаг процесса или {@link ProcessState#NONE}, если процесс не определён или не содержит шагов
     */
    public static ProcessState getFirstStep(ProcessType type) {
        List<ProcessState> steps = PROCESS_STEPS.get(type);
        return (steps != null && !steps.isEmpty()) ? steps.get(0) : ProcessState.NONE;
    }
    /**
     * Проверяет, является ли указанное состояние последним шагом в рамках заданного типа процесса.
     *
     * @param type  тип процесса
     * @param state состояние (шаг) для проверки
     * @return {@code true}, если состояние — последний шаг; {@code false} в противном случае
     */
    public static boolean isLastStep(ProcessType type, ProcessState state) {
        List<ProcessState> steps = PROCESS_STEPS.get(type);
        if (steps == null || steps.isEmpty()) return false;
        return steps.indexOf(state) == steps.size() - 1;
    }
    /**
     * Возвращает порядковый номер шага (начиная с 1) для указанного состояния в рамках типа процесса.
     *
     * @param type  тип процесса
     * @param state состояние (шаг)
     * @return номер шага (1-based), или {@code -1}, если состояние не найдено или процесс не определён
     */
    public static int getStepNumber(ProcessType type, ProcessState state) {
        List<ProcessState> steps = PROCESS_STEPS.get(type);
        if (steps == null) return -1;
        int index = steps.indexOf(state);
        return index >= 0 ? index + 1 : -1;
    }
    /**
     * Возвращает общее количество шагов для указанного типа процесса.
     *
     * @param type тип процесса
     * @return количество шагов, или {@code 0}, если процесс не определён
     */
    public static int getQuantityOfSteps(ProcessType type) {
        List<ProcessState> steps = PROCESS_STEPS.get(type);
        return steps != null ? steps.size() : 0;
    }
}
