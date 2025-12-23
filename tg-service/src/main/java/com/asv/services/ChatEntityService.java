package com.asv.services;


/**
 * Сервис для работы с токенами в БД.
 */
public interface ChatEntityService {
    /**
     * Создаёт новую запись чата в базе данных, связывая идентификатор чата с токеном аутентификации.
     *
     * @param chatId уникальный идентификатор чата (например, Telegram chat ID)
     * @param token  токен аутентификации, связанный с пользователем
     * @return {@code true}, если запись успешно создана; {@code false} в случае ошибки или дубликата
     */
    boolean creteChatEntity(long chatId, String token);

    /**
     * Удаляет запись чата из базы данных по её уникальному идентификатору.
     *
     * @param id идентификатор записи в базе данных
     * @return {@code true}, если запись успешно удалена; {@code false}, если запись не найдена или произошла ошибка
     */
    boolean deleteChatById(Long id);

    /**
     * Удаляет запись чата из базы данных по токену аутентификации.
     *
     * @param token токен аутентификации, связанный с чатом
     * @return {@code true}, если запись успешно удалена; {@code false}, если запись с указанным токеном не найдена или произошла ошибка
     */
    boolean deleteChatByToken(String token);

}
