package com.asv.hotel.dto.reportdto;

import com.asv.hotel.entities.enums.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Модель данных запроса для создания отчета.
 * <p>
 * Содержит информацию о типе отчета и номере комнаты, в которой произошло событие.
 */
@Getter
@Setter
@Schema(name = "Модель данных запроса для создания отчета")
public class CreateReportForm {

    /**
     * Тип отчета: либо отчет о работе ({@code WORK}), либо сообщение о проблеме ({@code ISSUE}).
     * <p>
     * Обязательное поле. Допустимые значения: {@code "ISSUE"}, {@code "WORK"}.
     */
    @Schema(description = "Тип отчета (отчет о работе или сообщение  о проблеме)", allowableValues = {"ISSUE", "WORK"})
    @NotNull
    private ReportType reportType;

    /**
     * Номер комнаты, связанной с отчетом.
     * <p>
     * Обязательное поле. Максимальная длина — 10 символов.
     */
    @Schema(description = "номер комнаты", example = "101")
    @NotNull
    @Size(max = 10)
    private String roomNumber;

    @Schema(description = "приложенные файлы для отчета, только форматы jpeg,png,jpg, не обязательно для заполнения")
    List<MultipartFile> multipartFileList;
}
