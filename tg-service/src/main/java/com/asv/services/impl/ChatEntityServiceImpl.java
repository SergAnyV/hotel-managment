package com.asv.services.impl;

import com.asv.entities.ChatEntity;
import com.asv.repositories.ChatEntityRepository;
import com.asv.services.ChatEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Реализация сервиса для управления привязками чатов Telegram к JWT-токенам.
 * Обеспечивает:
 * <ul>
 *   <li>Сохранение новой привязки chatId → токен</li>
 *   <li>Удаление привязки по идентификатору записи или по токену</li>
 *   <li>Фоновую очистку устаревших записей (старше 10 минут)</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatEntityServiceImpl implements ChatEntityService {

    private final ChatEntityRepository chatEntityRepository;
    /**
     * Временной лимит неактивности в миллисекундах (10 минут).
     * Используется для определения устаревших записей, подлежащих автоматической очистке.
     */
    private static final long NOT_ACTIVE_TIME_LIMIT = 10L * 60 * 1000;

    /**
     * Создаёт новую запись о привязке чата к токену аутентификации.
     *
     * @param chatId уникальный идентификатор чата в Telegram
     * @param token  JWT-токен, полученный при успешной аутентификации пользователя
     * @return {@code true}, если запись успешно сохранена в базе данных; {@code false} в случае ошибки
     */
    public boolean creteChatEntity(long chatId, String token) {

        ChatEntity chatEntity = ChatEntity.builder().chatId(chatId).token(token).build();
        try {
            chatEntityRepository.save(chatEntity);
            return true;
        } catch (RuntimeException e) {
            log.error("Problem with saving entity to DB {}", e.getMessage());
            return false;
        }
    }

    /**
     * Удаляет запись из базы данных по её внутреннему идентификатору.
     *
     * @param id идентификатор записи в таблице
     * @return {@code true}, если запись была найдена и удалена; {@code false}, если запись не найдена
     */
    public boolean deleteChatById(Long id) {
        return chatEntityRepository.deleteByChatId(id) != 0;
    }

    /**
     * Удаляет запись из базы данных по JWT-токену.
     *
     * @param token токен аутентификации
     * @return {@code true}, если запись была найдена и удалена; {@code false}, если запись не найдена
     */
    public boolean deleteChatByToken(String token) {
        return chatEntityRepository.deleteByToken(token) != 0;
    }

    /**
     * Фоновая задача, запускаемая каждые 5 минут (300000 мс).
     * Удаляет все записи, которые были созданы более (NOT_ACTIVE_TIME_LIMIT) 10 минут назад
     * (считаются неактивными и больше не нужны для безопасности).
     */
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void cleanupOldChats() {
        long tenMinutesAgo = System.currentTimeMillis() - NOT_ACTIVE_TIME_LIMIT;
        chatEntityRepository.deleteOlderThan(tenMinutesAgo);
    }
}
