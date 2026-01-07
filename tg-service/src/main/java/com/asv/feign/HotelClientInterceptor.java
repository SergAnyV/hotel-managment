package com.asv.feign;

import com.asv.repositories.ChatEntityRepository;
import com.asv.services.StringText;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Interceptor для Feign-клиентов.
 * Автоматически добавляет заголовок Authorization: Bearer <token> для методов с @AuthHeaderFeign.
 * Токен берётся из БД по chatId, который устанавливается в ChatContext.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HotelClientInterceptor implements RequestInterceptor {
    private final ChatEntityRepository chatEntityRepository;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long chatId = ChatContext.getChatId();

        if (chatId == null) {
            log.warn("ChatId не установлен в ThreadLocal для запроса: {}", requestTemplate.url());
            return;
        }

        if (shouldAddInterceptorChatIdToken(requestTemplate)) {
            try {
                String token = chatEntityRepository.findTokenByChatId(chatId).orElseThrow(() -> new RuntimeException());
                requestTemplate.header(StringText.AUTHORIZATION, StringText.BEARER + token);
            } catch (Exception e) {
                log.warn("Токен не найден для chatId={}, но требуется авторизация", chatId);
            }
        }
    }


    private boolean shouldAddInterceptorChatIdToken(RequestTemplate requestTemplate) {

        return requestTemplate.methodMetadata().method().isAnnotationPresent(AuthHeaderFeign.class);
    }


}
