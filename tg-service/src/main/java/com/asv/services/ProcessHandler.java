package com.asv.services;

import com.asv.enums.ProcessType;
import com.asv.sessions.UserSession;

public interface ProcessHandler {

    /**
     * Может ли этот обработчик обработать данный тип процесса
     */
    boolean canHandle(ProcessType processType);

    /**
     * Начать процесс (вызывается когда пользователь выбирает процесс из меню)
     */
    void startProcess(long chatId, UserSession session);

    /**
     * Обработать шаг процесса (вызывается когда пользователь вводит данные)
     */
    void handleStep(long chatId, String userInput, UserSession session, int messageId);

}
