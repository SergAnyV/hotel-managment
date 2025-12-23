package com.asv.hotel.configurations;

import com.asv.hotel.entities.enums.ReportType;
import com.asv.hotel.exceptions.HotelIncorrectInputData;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class  StringToReportTypeConverter implements Converter<String, ReportType> {
    @Override
    public ReportType convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        try {
            return ReportType.valueOf(source.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new HotelIncorrectInputData(String.format("Недопустимое значение ReportType: %s", source));
        }
    }
}
