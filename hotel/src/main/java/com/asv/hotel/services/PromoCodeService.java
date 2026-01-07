package com.asv.hotel.services;

import com.asv.hotel.dto.promocodedto.PromoCodeDTO;

import java.util.List;

/**
 * Сервис для управления промокодами в системе бронирования отеля.
 * <p>
 * Предоставляет базовые операции: создание, удаление и получение списка всех промокодов.
 * </p>
 */
public interface PromoCodeService {

    /**
     * Создаёт новый промокод.
     *
     * @param promoCodeDTO данные нового промокода
     * @return DTO созданного промокода
     */
    PromoCodeDTO createPromoCode(PromoCodeDTO promoCodeDTO);

    /**
     * Удаляет промокод по его текстовому коду.
     *
     * @param code текстовый код промокода (регистронезависимо)
     */
    void deletePromoCodeByCode(String code);

    /**
     * Возвращает список всех промокодов (активных и неактивных).
     *
     * @return список DTO всех промокодов; пустой список, если промокоды отсутствуют
     */
    List<PromoCodeDTO> findAllPromoCodesDTO();
}
