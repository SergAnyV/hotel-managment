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

@Service
@RequiredArgsConstructor
@Slf4j
public class PromoCodeServiceImpl implements PromoCodeInternalService {
    private final PromoCodeRepository promoCodeRepository;

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

    @Transactional
    public PromoCode findActivePromoCodeByName(String code) {
        return promoCodeRepository.findActivePromoCodeByCode(code).orElse(null);
    }

    @Transactional
    public void deletePromoCodeByCode(String code) {
        if (promoCodeRepository.deleteByCode(code) == 0) {
            log.warn("Warning: такого промокода не существует", code);
            throw new HotelDataNotFoundException(String.format("Данный промокод не существует '%s'", code));
        }
    }

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
