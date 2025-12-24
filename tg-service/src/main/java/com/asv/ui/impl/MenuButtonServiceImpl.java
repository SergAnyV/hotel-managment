package com.asv.ui.impl;

import com.asv.ui.KeyboardVariables;
import com.asv.ui.MenuButtonService;
import com.asv.util.KeyboardMenuFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * Реализация сервиса MenuButtonService клавиатур.
 */
@Service
public class MenuButtonServiceImpl implements MenuButtonService {
    /**
     * Для неавторизованных пользователей. Начальное меню
     */
    public ReplyKeyboardMarkup startMenuButtons() {
        return KeyboardMenuFactory.builder()
                .addButton(KeyboardVariables.LOGIN_PASSWORD_BUTTON)
                .addButton(KeyboardVariables.REGISTRATION_BUTTON)
                .newRow()
                .addButton(KeyboardVariables.GET_FREE_ROOMS_FOR_DATES_BUTTON)
                .newRow()
                .addButton(KeyboardVariables.VIEW_ADDITION_SERVICES_BUTTON)
                .addButton(KeyboardVariables.VIEW_ROOMS_BUTTON)
                .newRow()
                .addButton(KeyboardVariables.GET_INFO_SERVICE_BUTTON)
                .addButton(KeyboardVariables.GET_INFO_ROOM_BUTTON)
                .build();
    }

    /**
     * Для авторизованных пользователей. Расширенное меню
     */
    public ReplyKeyboardMarkup authMenuButtons() {
        return KeyboardMenuFactory.builder()
//                .addButtons(KeyboardVariables.UPDATE_PERSONAL_DATES_BUTTON, KeyboardVariables.BOOKING_ROOM_BUTTON)
                .addButtons( KeyboardVariables.BOOKING_ROOM_BUTTON)
                .newRow()
                .addButtons(KeyboardVariables.DELETE_BOOKING_BY_ID_BUTTON, KeyboardVariables.GET_FREE_ROOMS_FOR_DATES_BUTTON)
                .newRow()
                .addButtons(KeyboardVariables.VIEW_BOOKING_BY_ID_BUTTON, KeyboardVariables.VIEW_ADDITION_SERVICES_BUTTON)
                .newRow()
                .addButtons(KeyboardVariables.GET_INFO_ROOM_BUTTON, KeyboardVariables.GET_INFO_SERVICE_BUTTON)
                .newRow()
                .addButton(KeyboardVariables.EXIT_BUTTON)
                .build();

    }

    /**
     * Возврат в главное меню startMenuButtons. Кнопка возврата
     */
    public ReplyKeyboardMarkup backMenuButton() {
        return KeyboardMenuFactory.builder().addButton(KeyboardVariables.BACK_TO_MAIN_MENU_BUTTON).build();
    }




}
