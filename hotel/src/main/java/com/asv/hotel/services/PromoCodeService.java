package com.asv.hotel.services;

import com.asv.hotel.dto.promocodedto.PromoCodeDTO;

import java.util.List;

public interface PromoCodeService {

    PromoCodeDTO createPromoCode(PromoCodeDTO promoCodeDTO);

    void deletePromoCodeByCode(String code);

    List<PromoCodeDTO> findAllPromoCodesDTO();
}
