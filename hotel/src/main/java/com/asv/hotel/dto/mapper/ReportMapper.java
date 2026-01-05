package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.reportdto.ReportDTO;
import com.asv.hotel.entities.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper для преобразования сущности {@link Report} в DTO {@link ReportDTO}.
 * <p>
 * Использует MapStruct для автоматической генерации реализации метода преобразования.
 * </p>
 * <p>
 * При маппинге делегирует вложенные преобразования следующим мапперам:
 * <ul>
 *   <li>{@link ReportAttachmentMapper} — для вложений отчёта;</li>
 *   <li>{@link UserMapper} — для данных сотрудника;</li>
 *   <li>{@link RoomMapper} — для данных номера.</li>
 * </ul>
 * </p>
 * <p>
 * Поля даты и времени ({@code createdAt}, {@code updatedDate}) преобразуются
 * из {@link java.time.LocalDateTime} в {@link java.time.LocalDate}
 * с помощью кастомного метода {@code localDateTimeToLocalDate},
 * определённого в одном из используемых мапперов (например, в {@code ReportAttachmentMapper}).
 * </p>
 */
@Mapper(uses = {ReportAttachmentMapper.class, UserMapper.class, RoomMapper.class})
public interface ReportMapper {
    /**
     * Статический экземпляр маппера для прямого вызова без DI-контейнера.
     */
ReportMapper INSTANCE= Mappers.getMapper(ReportMapper.class);

    /**
     * Преобразует сущность {@link Report} в DTO {@link ReportDTO}.
     * <p>
     * Извлекает:
     * <ul>
     *   <li>идентификатор и статусы отчёта;</li>
     *   <li>тип отчёта и его описание;</li>
     *   <li>даты создания и обновления (без времени);</li>
     *   <li>номер комнаты через связь {@code report.room.number};</li>
     *   <li>никнейм сотрудника через связь {@code report.staff.nickName}.</li>
     * </ul>
     *
     * @param report исходная сущность отчёта
     * @return DTO с плоской структурой для передачи по API
     */
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
