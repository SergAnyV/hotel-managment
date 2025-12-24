package com.asv.repositories;

import com.asv.entities.ChatEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Репозиторий для работы с сущностью ChatElement.
 * Предоставляет методы для сохранения, поиска и обновления
 */
@Repository
public interface ChatEntityRepository extends CrudRepository<ChatEntity, Long> {

    /**
     * Находит токен по идентификатору чата.
     */
    @Query(value = "SELECT token FROM chat WHERE chat_id= :chatId", nativeQuery = true)
    Optional<String> findTokenByChatId(@Param("chatId") Long chatId);

    /**
     * Удаляет по chatId.
     */
    @Modifying
    @Query(value = "DELETE FROM chat WHERE chat_id= :id",nativeQuery = true)
    int deleteByChatId(@Param("id") Long chatId);

    /**
     * Удаляет по токену для существующего chatId.
     */
    @Modifying
    @Query(value = "DELETE FROM chat WHERE token= :token",nativeQuery = true)
    int deleteByToken(@Param("token") String token);

    /**
     * Проверяет, существует ли запись для данного chatId.
     */
    boolean existsByChatId(Long chatId);

    /**
     * Обновляет токен для существующего chatId.
     */
    @Modifying
    @Query(value = "UPDATE chat SET token = :token WHERE chat_id = :chatId",nativeQuery = true)
    int updateToken(@Param("chatId") Long chatId, @Param("token") String token);


    @Modifying
    @Query(value = "DELETE FROM chat WHERE created_at < :timeNow", nativeQuery = true)
    void deleteOlderThan(@Param("timeNow") long timeNow);
}
