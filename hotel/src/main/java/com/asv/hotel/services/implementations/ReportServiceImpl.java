package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.mapper.ReportMapper;
import com.asv.hotel.dto.reportdto.ReportDTO;
import com.asv.hotel.entities.Report;
import com.asv.hotel.entities.ReportAttachment;
import com.asv.hotel.entities.Room;
import com.asv.hotel.entities.User;
import com.asv.hotel.entities.enums.ReportStatus;
import com.asv.hotel.entities.enums.ReportType;
import com.asv.hotel.entities.enums.UserRole;
import com.asv.hotel.exceptions.HotelDataNotFoundException;
import com.asv.hotel.exceptions.HotelIncorrectInputData;
import com.asv.hotel.repositories.ReportRepository;
import com.asv.hotel.services.ReportAttachmentInternalService;
import com.asv.hotel.services.ReportService;
import com.asv.hotel.services.RoomInternalService;
import com.asv.hotel.services.UserInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Основная реализация сервиса управления отчётами ({@link ReportService}).
 * <p>
 * Обеспечивает:
 * </p>
 * <ul>
 *   <li>создание отчётов с автоматической привязкой к текущему пользователю и комнате,</li>
 *   <li>добавление вложений к существующим отчётам с валидацией форматов файлов,</li>
 *   <li>безопасное удаление вложений с проверкой прав доступа (только владелец отчёта или админ/менеджер).</li>
 * </ul>
 * <p>
 * Все методы, изменяющие данные, помечены аннотацией {@link org.springframework.transaction.annotation.Transactional},
 * что гарантирует целостность данных при возникновении ошибок.
 * </p>
 * <p>
 * Использует вспомогательные внутренние сервисы для работы с комнатами, пользователями и вложениями,
 * а также маппер для преобразования сущностей в DTO.
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final ReportAttachmentInternalService reportAAttachmentService;
    private final RoomInternalService roomService;
    private final UserInternalService userService;
    /**
     * Создаёт новый отчёт указанного типа для заданной комнаты.
     * <p>
     * Метод выполняет следующие шаги:
     * </p>
     * <ol>
     *   <li>Находит комнату по номеру (с удалением начальных и конечных пробелов).</li>
     *   <li>Если комната не найдена — возвращает {@code null}.</li>
     *   <li>Определяет текущего авторизованного пользователя как автора отчёта.</li>
     *   <li>Создаёт отчёт со статусом {@link com.asv.hotel.entities.enums.ReportStatus#ISSUED}.</li>
     *   <li>Если переданы файлы — преобразует их во вложения и привязывает к отчёту.</li>
     *   <li>Сохраняет отчёт (вместе со вложениями, если есть) в базе данных.</li>
     *   <li>Возвращает DTO созданного отчёта.</li>
     * </ol>
     *
     * @param reportType        тип отчёта (обязательный параметр)
     * @param roomNumber        номер комнаты (будет обрезан от пробелов)
     * @param multipartFileList список загружаемых файлов (может быть {@code null} или пустым)
     * @return {@link com.asv.hotel.dto.reportdto.ReportDTO} созданного отчёта или {@code null},
     *         если комната с указанным номером не существует
     * @throws RuntimeException при ошибках сохранения или обработки файлов (пробрасываются из зависимых сервисов)
     */
    @Transactional
    @Override
    public ReportDTO createReport(ReportType reportType,
                                  String roomNumber,
                                  List<MultipartFile> multipartFileList) {

        Room room = roomService.findRoomByNumber(roomNumber.trim());

        if (room == null) {
            return null;
        }

        User user = getUserFromSecurityContext();

        Report report = Report.builder().reportStatus(ReportStatus.ISSUED)
                .reportType(reportType)
                .room(room)
                .staff(user)
                .build();

        if (isCollectionNullOrEmpty(multipartFileList)) {
            reportRepository.save(report);
            return ReportMapper.INSTANCE.reportToReportDTO(report);
        }

        Set<ReportAttachment> reportAttachmentSet = reportAAttachmentService.generateReportAttachmentSetFromMultipartFileList(multipartFileList);
        report = addAttachmentToReport(report, reportAttachmentSet);
        report = reportRepository.save(report);

        return ReportMapper.INSTANCE.reportToReportDTO(report);
    }
    /**
     * Добавляет одно или несколько вложений к существующему отчёту.
     * <p>
     * Требования:
     * </p>
     * <ul>
     *   <li>Отчёт с указанным {@code reportId} должен существовать.</li>
     *   <li>Список файлов не должен быть пустым или {@code null}.</li>
     *   <li>Хотя бы один файл должен быть преобразован во вложение (проверяется по допустимым форматам).</li>
     * </ul>
     * <p>
     * Если условия не выполнены, выбрасываются соответствующие исключения.
     * Все вложения автоматически привязываются к отчёту через метод {@link com.asv.hotel.entities.Report#addAttachment(ReportAttachment)}.
     * </p>
     *
     * @param reportId          идентификатор существующего отчёта
     * @param multipartFileList непустой список файлов для прикрепления
     * @throws com.asv.hotel.exceptions.HotelDataNotFoundException если отчёт не найден
     * @throws com.asv.hotel.exceptions.HotelIncorrectInputData   если файлы отсутствуют,
     *                                                             пусты или не содержат допустимых форматов
     */
    @Transactional
    @Override
    public void addReportAttachmentToReport(Long reportId, List<MultipartFile> multipartFileList) {
        Report report = reportRepository.findReportById(reportId).orElse(null);

        if (report == null) {
            log.warn("Warning: Нет отчета с таким id {}", reportId);
            throw new HotelDataNotFoundException(String.format("Нет отчета с таким id %d", reportId));
        }

        if (isCollectionNullOrEmpty(multipartFileList)) {
            log.warn("Warning: нет приложенных файлов для сохранения количество");
            throw new HotelIncorrectInputData("Отсутствуют файлы для сохранения в отчет");
        }

        Set<ReportAttachment> reportAttachmentSet = multipartFileList.stream().
                map(mpf -> reportAAttachmentService.generateReportAttachmentFromMultipartFile(mpf)).
                filter(reportAttachment -> reportAttachment != null)
                .collect(Collectors.toSet());
        if (isCollectionNullOrEmpty(reportAttachmentSet)) {
            log.warn("Warning: в приложенных файлах нет нужных для сохранения форматов");
            throw new HotelIncorrectInputData("В приложенных файлах нет нужных для сохранения форматов");
        }
        for (ReportAttachment reportAttachment : reportAttachmentSet) {
            report.addAttachment(reportAttachment);
        }
        reportRepository.save(report);
    }
    /**
     * Удаляет конкретное вложение из отчёта по идентификаторам.
     * <p>
     * Права доступа:
     * </p>
     * <ul>
     *   <li><b>Администраторы и менеджеры</b> могут удалять любые вложения из любого отчёта.</li>
     *   <li><b>Обычные сотрудники</b> могут удалять только вложения из отчётов, которые они сами создали.</li>
     * </ul>
     * <p>
     * Удаление выполняется напрямую через нативный SQL-запрос с проверкой принадлежности вложения отчёту.
     * Если вложение не найдено или не принадлежит указанному отчёту, выбрасывается исключение.
     * </p>
     *
     * @param reportId           идентификатор отчёта
     * @param reportAttachmentId идентификатор удаляемого вложения
     * @throws com.asv.hotel.exceptions.HotelDataNotFoundException если отчёт не существует
     *                                                             или у пользователя нет прав на удаление
     * @throws com.asv.hotel.exceptions.HotelIncorrectInputData   если вложение с таким ID не найдено
     *                                                             или не принадлежит указанному отчёту
     */
    @Transactional
    @Override
    public void deleteAttachmentFromReport(Long reportId, Long reportAttachmentId) {
        User userRequester = getUserFromSecurityContext();
        if (isUserRoleAdminOrManager(userRequester)) {
            performDeletingByAttachmentIdAndReportId(reportId, reportAttachmentId);
            return;
        }

        User userOwner = getUserOwnerFromReport(reportRepository.findReportById(reportId));
        if (userRequester.getNickName().equals(userOwner.getNickName())) {
            performDeletingByAttachmentIdAndReportId(reportId, reportAttachmentId);
            return;
        }
        log.warn("не совпадение ролей  или владельцев запрашиваемых ресурсов " +
                "requester nick={} , role={} , owner nick={} , role={}",
                userRequester.getNickName(),
                userRequester.getType().getRole(),
                userOwner.getNickName(),
                userOwner.getType().getRole());
        throw new HotelDataNotFoundException("не совпадение ролей  или владельцев запрашиваемых ресурсов");

    }

    private User getUserOwnerFromReport(Optional<Report> reportOptional) {
        Report report = reportOptional.orElse(null);
        if (report == null) {
            log.warn("не существует отчета с таким номером");
            throw new HotelDataNotFoundException("не существует отчета с таким номером ");
        }
        return report.getStaff();
    }


    private boolean isCollectionNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private Report addAttachmentToReport(Report report, Set<ReportAttachment> reportAttachmentSet) {
        for (ReportAttachment reportAttachment : reportAttachmentSet) {
            report.addAttachment(reportAttachment);
        }
        return report;
    }

    private User getUserFromSecurityContext() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean isUserRoleAdminOrManager(User userRequester) {
        return userRequester.getType().getRole().equals(UserRole.ADMIN) ||
                userRequester.getType().getRole().equals(UserRole.MANAGER);
    }

    private void performDeletingByAttachmentIdAndReportId(Long reportId, Long reportAttachmentId) {
        int result = reportRepository.deleteReportAttachmentFromReportByID(reportId, reportAttachmentId);
        if (result == 0) {
            log.warn("Warning: для отчета id {} не существует пиложения с id {}", reportId, reportAttachmentId);
            throw new HotelIncorrectInputData(String.format("для отчета id %d не существует приложения с id %d", reportId, reportAttachmentId));
        }
    }
}
