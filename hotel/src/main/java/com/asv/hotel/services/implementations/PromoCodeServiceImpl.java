package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.promocodedto.PromoCodeDTO;
import com.asv.hotel.dto.mapper.PromoCodeMapper;
import com.asv.hotel.entities.PromoCode;
import com.asv.hotel.exceptions.HotelDataAlreadyExistsException;
import com.asv.hotel.exceptions.HotelDataNotFoundException;
import com.asv.hotel.repositories.PromoCodeRepository;
import com.asv.hotel.services.PromoCodeInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления промокодами.
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>создание новых промокодов с проверкой уникальности;</li>
 *   <li>поиск активных промокодов по коду;</li>
 *   <li>удаление промокодов по коду;</li>
 *   <li>получение списка всех промокодов.</li>
 * </ul>
 * </p>
 * <p>
 * Все операции выполняются в транзакциях для обеспечения целостности данных.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PromoCodeServiceImpl implements PromoCodeInternalService {
    private final PromoCodeRepository promoCodeRepository;

    /**
     * Создаёт новый промокод.
     * <p>
     * Перед сохранением проверяется, не существует ли промокод с таким кодом.
     * Если существует — выбрасывается исключение.
     * </p>
     *
     * @param promoCodeDTO данные нового промокода
     * @return DTO созданного промокода
     * @throws HotelDataAlreadyExistsException если промокод с таким кодом уже существует
     */
    @Transactional
    public PromoCodeDTO createPromoCode(PromoCodeDTO promoCodeDTO) {
        if (promoCodeRepository.findPromoCodesByCode(promoCodeDTO.getCode()).isPresent()) {
            log.warn("Warning такой промокод уже существет", promoCodeDTO.getCode());
            throw new HotelDataAlreadyExistsException(String.format("Данный промокод уже существует '%s'",
                    promoCodeDTO.getCode()));
        }
        PromoCode promoCode = PromoCodeMapper.INSTANCE.promoCodeDTOToPromoCode(promoCodeDTO);
        return PromoCodeMapper.INSTANCE.promoCodeToPromoCodeDTO(promoCodeRepository.save(promoCode));
    }


    /**
     * Находит активный промокод по коду (регистронезависимо).
     *
     * @param code текстовый код промокода
     * @return сущность промокода, если он существует и активен; {@code null}, если не найден
     */
    @Transactional
    public PromoCode findActivePromoCodeByName(String code) {
        return promoCodeRepository.findActivePromoCodeByCode(code).orElse(null);
    }

    /**
     * Удаляет промокод по коду (регистронезависимо).
     *
     * @param code текстовый код промокода
     * @throws HotelDataNotFoundException если промокод с указанным кодом не найден
     */
    @Transactional
    public void deletePromoCodeByCode(String code) {
        if (promoCodeRepository.deleteByCode(code) == 0) {
            log.warn("Warning: такого промокода не существует", code);
            throw new HotelDataNotFoundException(String.format("Данный промокод не существует '%s'", code));
        }
    }

    /**
     * Возвращает список всех промокодов (активных и неактивных).
     *
     * @return список DTO всех промокодов; пустой список, если промокоды отсутствуют
     */
    @Transactional
    public List<PromoCodeDTO> findAllPromoCodesDTO() {
        List<PromoCode> listPromo = promoCodeRepository.findAll();
        if (listPromo.isEmpty()) {
            log.warn("Warning промокодов нет");
            return Collections.emptyList();
        }
        return listPromo.stream().map(promo -> {
            return PromoCodeMapper.INSTANCE.promoCodeToPromoCodeDTO(promo);

        }).collect(Collectors.toList());
    }

    // TODO : переделать для админа и менеджера для изменений см. репорт сервисы
}
