package com.asv.hotel.configurations;

import com.asv.hotel.entities.enums.ReportType;
import com.asv.hotel.exceptions.HotelIncorrectInputData;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
/**
 * Конвертер строки в перечисление {@link ReportType}.
 * <p>
 * Используется Spring Framework для автоматического преобразования строковых значений,
 * переданных, например, через HTTP-параметры запроса или форму, в соответствующий элемент
 * перечисления {@link ReportType}.
 * </p>
 * <p>
 * Поддерживает:
 * <ul>
 *   <li>Пробельные символы в начале и конце строки (они удаляются с помощью {@link String#trim()}).</li>
 *   <li>Регистронезависимый ввод (входное значение приводится к верхнему регистру).</li>
 * </ul>
 * </p>
 * <p>
 * Если входная строка {@code null}, пустая или содержит только пробелы,
 * метод {@link #convert(String)} возвращает {@code null}.
 * </p>
 * <p>
 * В случае, если строка не соответствует ни одному из значений перечисления {@link ReportType},
 * выбрасывается исключение {@link HotelIncorrectInputData}.
 * </p>
 *
 * @see Converter
 * @see ReportType
 * @see HotelIncorrectInputData
 */
@Component
public class  StringToReportTypeConverter implements Converter<String, ReportType> {
    /**
     * Преобразует строку в значение перечисления {@link ReportType}.
     *
     * @param source входная строка для преобразования; может быть {@code null} или содержать только пробелы
     * @return соответствующее значение {@link ReportType}, или {@code null}, если входная строка пуста
     * @throws HotelIncorrectInputData если строка не соответствует ни одному из допустимых значений перечисления
     */
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
