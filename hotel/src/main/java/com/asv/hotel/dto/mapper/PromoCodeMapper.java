package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.promocodedto.PromoCodeDTO;
import com.asv.hotel.entities.PromoCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
/**
 * Mapper для преобразования между сущностью {@link PromoCode} и её DTO-представлением {@link PromoCodeDTO}.
 * <p>
 * Использует библиотеку MapStruct для генерации эффективного и типобезопасного кода конвертации
 * между доменной моделью промокодов и объектами передачи данных (DTO).
 * </p>
 * <p>
 * Основные сценарии использования:
 * <ul>
 *   <li>Сериализация промокода в ответе ({@link PromoCode} → {@link PromoCodeDTO});</li>
 *   <li>Десериализация входящих данных при создании или обновлении промокода
 *       ({@link PromoCodeDTO} → {@link PromoCode}), с игнорированием идентификатора.</li>
 * </ul>
 * </p>
 */
@Mapper
public interface PromoCodeMapper {

    /**
     * Экземпляр маппера, созданный через фабрику MapStruct.
     * <p>
     * Предоставляет доступ к сгенерированным методам без необходимости внедрения через Spring.
     * </p>
     */
    PromoCodeMapper INSTANCE= Mappers.getMapper(PromoCodeMapper.class);

    /**
     * Преобразует сущность {@link PromoCode} в DTO {@link PromoCodeDTO}.
     * <p>
     * Копирует все поля, включая код, тип скидки, значение, даты действия и статус активности.
     * Используется при отправке данных о промокоде клиенту (например, в ответе на GET-запрос).
     * </p>
     *
     * @param promoCode сущность промокода из базы данных
     * @return DTO с полной информацией о промокоде
     */
    PromoCodeDTO promoCodeToPromoCodeDTO(PromoCode promoCode);

    /**
     * Преобразует DTO {@link PromoCodeDTO} в сущность {@link PromoCode}.
     * <p>
     * Все поля из DTO копируются в сущность, за исключением идентификатора ({@code id}),
     * который игнорируется с помощью {@code @Mapping(target = "id", ignore = true)}.
     * Это необходимо, чтобы избежать перезаписи ID при создании нового промокода
     * или некорректного обновления существующего.
     * @param promoCodeDTO входящий DTO с данными промокода
     * @return сущность для сохранения или обновления в БД (без ID)
     */
    @Mapping(target = "id",ignore = true)
    PromoCode promoCodeDTOToPromoCode(PromoCodeDTO promoCodeDTO);

}
