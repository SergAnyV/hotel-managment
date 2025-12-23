package com.asv.hotel.services;

import com.asv.hotel.entities.PromoCode;

import java.util.Optional;

public interface PromoCodeInternalService extends PromoCodeService{

   PromoCode findActivePromoCodeByName(String code);
}
