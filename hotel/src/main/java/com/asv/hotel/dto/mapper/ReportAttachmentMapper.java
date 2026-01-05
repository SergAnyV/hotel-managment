package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.reportattachmendto.ReportAttachmentDTO;
import com.asv.hotel.dto.reportattachmendto.ReportAttachmentSimpleDTO;
import com.asv.hotel.entities.ReportAttachment;
import com.asv.hotel.exceptions.HotelReportAttachmentException;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
/**
 * Mapper для преобразования между сущностью {@link ReportAttachment} и её DTO-представлениями.
 * <p>
 * Использует MapStruct для генерации реализаций методов преобразования.
 * Поддерживает:
 * <ul>
 *   <li>преобразование {@link ReportAttachment} → {@link ReportAttachmentDTO};</li>
 *   <li>преобразование {@link ReportAttachment} → {@link ReportAttachmentSimpleDTO};</li>
 *   <li>создание {@link ReportAttachment} из {@link MultipartFile} (без указания типа отчёта).</li>
 * </ul>
 * </p>
 * <p>
 * Включает кастомные методы преобразования для работы с датами, файлами и ресурсами.
 * </p>
 */
@Mapper
public interface ReportAttachmentMapper {
    /**
     * Экземпляр маппера для использования без инъекции (через статический фабричный метод MapStruct).
     */
    ReportAttachmentMapper INSTANCE = Mappers.getMapper(ReportAttachmentMapper.class);

    /**
     * Преобразует сущность {@link ReportAttachment} в DTO {@link ReportAttachmentDTO}.
     * <p>
     * Поле {@code createdAt} (типа {@link LocalDateTime}) преобразуется в {@link LocalDate}.
     *
     * @param reportAttachment исходная сущность
     * @return DTO с данными вложения отчёта
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToLocalDate")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "contentType", source = "contentType")
    @Mapping(target = "size", source = "size")
    ReportAttachmentDTO reportAttachmentToReportAttachmentDTO(ReportAttachment reportAttachment);

    /**
     * Создаёт сущность {@link ReportAttachment} из {@link MultipartFile}.
     * <p>
     * Извлекает:
     * <ul>
     *   <li>байты файла → {@code content};</li>
     *   <li>имя поля формы → {@code fileName} (внимание: это НЕ оригинальное имя файла!);</li>
     *   <li>размер → {@code size}.</li>
     * </ul>
     * </p>
     * <p>
     * ⚠️ {@code multipartFile.getName()} возвращает имя <b>поля формы</b>, а не имя загруженного файла.
     * Для получения оригинального имени файла следует использовать {@code multipartFile.getOriginalFilename()}.
     * </p>
     *
     * @param multipartFile входной файл из HTTP-запроса
     * @return сущность {@link ReportAttachment} без указания типа отчёта
     * @throws HotelReportAttachmentException если произошла ошибка при чтении байтов файла
     */
    @Mapping(target = "content",source = "multipartFile", qualifiedByName = "multiPartFileToByteArray")
    @Mapping(target = "fileName",source = "multipartFile", qualifiedByName = "multiPartFileGetName")
    @Mapping(target = "size",source = "multipartFile", qualifiedByName = "multiPartFileGetSize")
    ReportAttachment multipartFileToReportAttachmentWithoutType(MultipartFile multipartFile);

    /**
     * Преобразует сущность {@link ReportAttachment} в упрощённое DTO {@link ReportAttachmentSimpleDTO}.
     * <p>
     * Поле {@code content} (байтовый массив) преобразуется в {@link ByteArrayResource}
     * для удобной передачи в HTTP-ответах.
     *
     * @param reportAttachment исходная сущность
     * @return упрощённое DTO с ресурсом содержимого
     */
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "contentType", source = "contentType")
    @Mapping(target = "byteArrayResource",source = "content",qualifiedByName = "byteArrayToByteArrayResource")
    ReportAttachmentSimpleDTO reportAttachmentToReportAttachmentSimpleDTO(ReportAttachment reportAttachment);

    /**
     * Преобразует {@link MultipartFile} в массив байтов.
     *
     * @param multipartFile файл из запроса
     * @return массив байтов содержимого
     * @throws HotelReportAttachmentException при ошибке чтения (например, IOException)
     */
    @Named("multiPartFileToByteArray")
    default byte[] multiPartFileToByteArray(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new HotelReportAttachmentException(String.format("Неверное преобразование из MultipartFile в byte[] '%s' ",
                    e));
        }
    }

    /**
     * Преобразует {@link LocalDateTime} в {@link LocalDate}.
     *
     * @param localDateTime исходная дата-время
     * @return дата без времени
     */
    @Named("localDateTimeToLocalDate")
    default LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    @Named("multiPartFileGetName")
    default String multiPartFileGetName(MultipartFile multipartFile) {
        return multipartFile.getName();
    }

    /**
     * Возвращает размер файла из {@link MultipartFile}.
     *
     * @param multipartFile файл из запроса
     * @return размер в байтах
     */
    @Named("multiPartFileGetSize")
    default Long multiPartFileGetSize(MultipartFile multipartFile) {
        return multipartFile.getSize();
    }


    /**
     * Преобразует массив байтов в {@link ByteArrayResource}.
     *
     * @param bytes исходный массив
     * @return ресурс, совместимый с Spring для отправки в HTTP-ответе
     */
    @Named("byteArrayToByteArrayResource")
    default ByteArrayResource byteArrayToByteArrayResource(byte[] bytes){
        return new ByteArrayResource(bytes);
    }

}
