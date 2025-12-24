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

@Mapper
public interface ReportAttachmentMapper {
    ReportAttachmentMapper INSTANCE = Mappers.getMapper(ReportAttachmentMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToLocalDate")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "contentType", source = "contentType")
    @Mapping(target = "size", source = "size")
    ReportAttachmentDTO reportAttachmentToReportAttachmentDTO(ReportAttachment reportAttachment);


    @Mapping(target = "content",source = "multipartFile", qualifiedByName = "multiPartFileToByteArray")
    @Mapping(target = "fileName",source = "multipartFile", qualifiedByName = "multiPartFileGetName")
    @Mapping(target = "size",source = "multipartFile", qualifiedByName = "multiPartFileGetSize")
    ReportAttachment multipartFileToReportAttachmentWithoutType(MultipartFile multipartFile);

    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "contentType", source = "contentType")
    @Mapping(target = "byteArrayResource",source = "content",qualifiedByName = "byteArrayToByteArrayResource")
    ReportAttachmentSimpleDTO reportAttachmentToReportAttachmentSimpleDTO(ReportAttachment reportAttachment);


    @Named("multiPartFileToByteArray")
    default byte[] multiPartFileToByteArray(MultipartFile multipartFile) {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new HotelReportAttachmentException(String.format("Неверное преобразование из MultipartFile в byte[] '%s' ",
                    e));
        }
    }

    @Named("localDateTimeToLocalDate")
    default LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    @Named("multiPartFileGetName")
    default String multiPartFileGetName(MultipartFile multipartFile) {
        return multipartFile.getName();
    }

    @Named("multiPartFileGetSize")
    default Long multiPartFileGetSize(MultipartFile multipartFile) {
        return multipartFile.getSize();
    }

    @Named("byteArrayToByteArrayResource")
    default ByteArrayResource byteArrayToByteArrayResource(byte[] bytes){
        return new ByteArrayResource(bytes);
    }

}
