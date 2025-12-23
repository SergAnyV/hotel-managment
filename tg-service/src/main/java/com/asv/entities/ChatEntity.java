package com.asv.entities;


import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность для хранения данных чата в базе данных.
 * - chatId: уникальный идентификатор чата в Telegram.
 * - token: JWT-токен, полученный после успешной авторизации.
 * ВАЖНО: Токен храниться в зашифрованном виде.
 */
@Entity
@Table(name = "chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEntity {

    @Id
    private Long chatId;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "created_at", nullable = false)
    long createTime;

    @PrePersist
    private void preUpdate() {
        this.createTime = System.currentTimeMillis();
    }


}
