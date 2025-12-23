package com.asv.util;

import com.asv.exceptions.TGKeyboardException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
/**
 * Утилитарный класс для построения клавиатур Telegram.
 * Не предназначен для создания экземпляров.
 */
public final class KeyboardMenuFactory {

    private final List<KeyboardRow> rows = new ArrayList<>();
    private KeyboardRow currentRow;

    private KeyboardMenuFactory() {

    }

    /**
     * Старт построения новой клавиатуры
     */
    public static KeyboardMenuFactory builder() {
        return new KeyboardMenuFactory();
    }

    /**
     * Начинает новый ряд кнопок
     */
    public KeyboardMenuFactory newRow() {
        if (currentRow != null && !currentRow.isEmpty()) {
            rows.add(currentRow);
        }
        currentRow = new KeyboardRow();
        return this;
    }

    /**
     * Добавляет кнопку в текущий ряд
     */
    public KeyboardMenuFactory addButton(String text) {
        if (text == null || text.strip().isBlank()) {
            throw new TGKeyboardException("Текст кнопки не может быть пустым");
        }

        if (currentRow == null) {
            currentRow = new KeyboardRow();
        }

        currentRow.add(text);
        return this;
    }

    /**
     * Добавляет несколько кнопок в текущий ряд
     */
    public KeyboardMenuFactory addButtons(String... texts) {
        for (String text : texts) {
            addButton(text);
        }
        return this;
    }

    /**
     * Завершает построение и возвращает готовую клавиатуру.
     */
    public ReplyKeyboardMarkup build() {
        if (currentRow != null && !currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        if (rows.isEmpty()) {
            throw new TGKeyboardException("Нет клавиатуры для построения");
        }

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .isPersistent(true)
                .build();
    }

}
