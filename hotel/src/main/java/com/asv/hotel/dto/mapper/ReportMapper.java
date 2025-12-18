package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.reportdto.ReportDTO;
import com.asv.hotel.entities.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ReportAttachmentMapper.class, UserMapper.class, RoomMapper.class})
public interface ReportMapper {
ReportMapper INSTANCE= Mappers.getMapper(ReportMapper.class);
    @Mapping(target = "id",source = "id")
    @Mapping(target = "reportStatus",source = "reportStatus")
    @Mapping(target = "descriptionStatus",source = "descriptionStatus")
    @Mapping(target = "reportType",source = "reportType")
    @Mapping(target = "descriptionType",source = "descriptionType")
    @Mapping(target = "createdAt",source = "createdAt",qualifiedByName = "localDateTimeToLocalDate")
    @Mapping(target = "updatedDate",source = "updatedDate",qualifiedByName = "localDateTimeToLocalDate")
    @Mapping(target = "roomNumber",source = "room.number")
    @Mapping(target = "ownerNickName",source = "staff.nickName")
    ReportDTO reportToReportDTO(Report report);

}
