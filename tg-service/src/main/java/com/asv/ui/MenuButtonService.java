package com.asv.ui;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
/**
 * Сервис для получения клавиатур.
 */
public interface MenuButtonService {
    /**
     * Для неавторизованных пользователей
     */
    ReplyKeyboardMarkup startMenuButtons();
    /**
     * Для авторизованных пользователей
     */
    ReplyKeyboardMarkup authMenuButtons();
    /**
     * Возврат в главное меню startMenuButtons.
     */
    ReplyKeyboardMarkup backMenuButton();

}
