package com.asv.hotel.util;

import lombok.extern.slf4j.Slf4j;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public class BookingUtils {

    public static BigDecimal calculateLivingDays(LocalDate from,LocalDate till){
    if (!from.isBefore(till)){
        log.warn("неверно указаны даты заселение {} выселение {}",from,till);
        return null;
    }
    return new BigDecimal(from.until(till, ChronoUnit.DAYS));
    }
    public  static LocalDateTime convertDateToDateTimeNoon(LocalDate localDate){
        return LocalDateTime.of(localDate, LocalTime.NOON);
    }



}
