package com.asv.services.impl;

import com.asv.enums.ProcessType;
import com.asv.services.ProcessHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Фабрика для получения обработчиков пошаговых процессов по их типу.
 * Автоматически собирает все бины, реализующие {@link ProcessHandler},
 * и регистрирует их в внутренней карте, сопоставляя с поддерживаемым {@link ProcessType}.
 * Инициализация происходит один раз при старте приложения.
 */
@Service
@RequiredArgsConstructor
public class ProcessHandlerFactory {
    private final List<ProcessHandler> handlers;
    private final Map<ProcessType, ProcessHandler> handlerMap = new HashMap<>();

    /**
     * Инициализирует фабрику: перебирает все доступные обработчики и регистрирует
     * их в {@link #handlerMap}, сопоставляя с первым подходящим {@link ProcessType},
     * который они поддерживают (согласно методу {@link ProcessHandler#canHandle(ProcessType)}).
     * Вы
     */
    @PostConstruct
    public void init() {
        for (ProcessHandler handler : handlers) {

            for (ProcessType type : ProcessType.values()) {
                if (handler.canHandle(type)) {
                    handlerMap.put(type, handler);
                    break;
                }
            }

        }
    }

    /**
     * Возвращает обработчик, зарегистрированный для указанного типа процесса.
     *
     * @param processType тип процесса, для которого требуется обработчик
     * @return экземпляр {@link ProcessHandler}, поддерживающий указанный тип
     * @throws IllegalArgumentException если обработчик для данного типа не найден
     */
    public ProcessHandler getHandler(ProcessType processType) {
        ProcessHandler handler = handlerMap.get(processType);
        if (handler == null) {
            throw new IllegalArgumentException(String.format("Не найден обработчик %s", processType));
        }
        return handler;
    }

    /**
     * Проверяет, зарегистрирован ли обработчик для указанного типа процесса.
     *
     * @param processType тип процесса
     * @return {@code true}, если обработчик существует; {@code false} в противном случае
     */
    public boolean hasHandler(ProcessType processType) {
        return handlerMap.containsKey(processType);
    }

}
