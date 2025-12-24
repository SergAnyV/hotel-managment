package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.mapper.ReportAttachmentMapper;
import com.asv.hotel.dto.reportattachmendto.ReportAttachmentSimpleDTO;
import com.asv.hotel.entities.ReportAttachment;
import com.asv.hotel.entities.User;
import com.asv.hotel.entities.enums.UserRole;
import com.asv.hotel.exceptions.HotelDataNotFoundException;
import com.asv.hotel.exceptions.HotelIncorrectInputData;
import com.asv.hotel.exceptions.HotelReportAttachmentException;
import com.asv.hotel.repositories.ReportAttachmentRepository;
import com.asv.hotel.services.ReportAttachmentInternalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Основная реализация сервиса для управления вложениями отчётов.
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>Безопасный доступ к данным с учётом ролей пользователя (ADMIN, MANAGER, STAFF)</li>
 *   <li>Валидацию и преобразование загружаемых файлов (только JPEG/PNG)</li>
 *   <li>Формирование ZIP-архивов из вложений отчёта</li>
 *   <li>Корректную работу с бинарными данными и метаданными файлов</li>
 * </ul>
 * <p>
 * Все операции с доступом используют {@link SecurityContextHolder} для получения
 * информации о текущем пользователе. Методы, возвращающие данные, гарантируют,
 * что пользователь имеет право на их получение.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ReportAttachmentServiceImpl implements ReportAttachmentInternalService {
    private final ReportAttachmentRepository reportAttachmentRepository;

    /**
     * {@inheritDoc}
     * <p>
     * Проверяет права доступа перед возвратом содержимого файла.
     */
    @Transactional
    @Override
    public ReportAttachmentSimpleDTO findReportAttachmentSimpleDTOByID(Long id) {
        ReportAttachment reportAttachment = reportAttachmentRepository.findById(id).orElse(null);
        if (reportAttachment == null) {
            return null;
        }

        User userRequester = getUserFromSecurityContext();
        if (isUserRoleAdminOrManager(userRequester)) {
            return ReportAttachmentMapper.INSTANCE.reportAttachmentToReportAttachmentSimpleDTO(reportAttachment);
        }

        User userOwner = getUserOwnerFromReportAttachment(reportAttachment);
        if (userRequester.getNickName().equals(userOwner.getNickName())) {
            return ReportAttachmentMapper.INSTANCE.reportAttachmentToReportAttachmentSimpleDTO(reportAttachment);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Удаляет вложение из базы данных. Операция защищена на уровне контроллера аннотацией {@code @PreAuthorize}.
     */
    @Transactional
    @Override
    public void deleteReportAttachmentById(Long id) {
        int result = reportAttachmentRepository.deleteReportAttachmentById(id);
        if (result == 0) {
            log.warn("Warning: нет файла для удаления с данным id {}", id);
            throw new HotelDataNotFoundException(String.format("Не существует файла для удаления с данным id = %s", id));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Простой запрос к репозиторию без дополнительной логики.
     */
    @Override
    public List<ReportAttachment> findReportAttachmentByReportID(Long id) {
        return reportAttachmentRepository.findReportAttachmentByReportID(id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Поддерживает только JPEG и PNG. Определяет тип по "магическим байтам".
     */
    @Override
    public ReportAttachment generateReportAttachmentFromMultipartFile(MultipartFile multipartFile) {
        String fileType = null;
        try {
            fileType = findFileTypePhoto(multipartFile);
        } catch (HotelReportAttachmentException | HotelIncorrectInputData e) {
            return null;
        }
        ReportAttachment reportAttachment = ReportAttachmentMapper.INSTANCE.multipartFileToReportAttachmentWithoutType(multipartFile);
        reportAttachment.setContentType(fileType);
        return reportAttachment;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Использует стрим для фильтрации и преобразования.
     */
    @Override
    public Set<ReportAttachment> generateReportAttachmentSetFromMultipartFileList(List<MultipartFile> multipartFileList) {
        return multipartFileList.stream().map(multipartFile ->
                        generateReportAttachmentFromMultipartFile(multipartFile)).
                filter(reportAttachment -> reportAttachment != null).
                collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Использует JPQL-запрос с JOIN FETCH для предварительной загрузки связанных сущностей.
     */
    @Override
    public List<ReportAttachment> findReportAttachmentForZipByReportID(Long id) {
        return reportAttachmentRepository.findReportAttachmentForZipListByReportId(id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Формирует ZIP-архив в памяти с использованием {@link ByteArrayOutputStream} и {@link ZipOutputStream}.
     * В случае ошибки возвращает пустой массив, чтобы избежать исключений на уровне контроллера.
     */
    @Transactional
    public byte[] findByteArrayAttachmentsLikeZipByReportID(Long id) {

        List<ReportAttachment> reportAttachmentList = reportAttachmentRepository.findReportAttachmentForZipListByReportId(id);

        User userRequester = getUserFromSecurityContext();
        if (isUserRoleAdminOrManager(userRequester)) {
            return getStreamingResponseBodyByReportID(reportAttachmentList);
        }
        User userOwner = getUserOwnerFromReportAttachment(reportAttachmentList.get(0));
        if (userOwner.getNickName().equals(userRequester.getNickName())) {
            return getStreamingResponseBodyByReportID(reportAttachmentList);
        }
        return new byte[0];
    }

    private String findFileTypePhoto(MultipartFile multipartFile) {
        byte[] data;
        try {
            data = multipartFile.getBytes();
        } catch (IOException e) {
            throw new HotelReportAttachmentException(String.format("Неверное преобразование из MultipartFile в byte[] '%s' ",
                    e));
        }
        if (data == null || data.length < 4) {
            throw new HotelIncorrectInputData("Некорректный тип входных данных файл поврежден или недостаточное количество байт");
        }

        if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8 && data[2] == (byte) 0xFF) {
            return "image/jpeg";
        }

        if (data[0] == (byte) 0x89 && data[1] == (byte) 0x50 && data[2] == (byte) 0x4E && data[3] == (byte) 0x47) {
            return "image/png";
        }

        throw new HotelIncorrectInputData("Неверный тип данных для фото");
    }

    private User getUserFromSecurityContext() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private boolean isUserRoleAdminOrManager(User userRequester) {
        return userRequester.getType().getRole().equals(UserRole.ADMIN) ||
                userRequester.getType().getRole().equals(UserRole.MANAGER);
    }

    private User getUserOwnerFromReportAttachment(ReportAttachment reportAttachment) {
        return reportAttachment.getReport().getStaff();
    }

    private byte[] getStreamingResponseBodyByReportID(List<ReportAttachment> reportAttachmentForZipDTOList) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {
            for (ReportAttachment reportAttachmentForZipDTO : reportAttachmentForZipDTOList) {
                ZipEntry entry = new ZipEntry(
                        String.format("%s %s", reportAttachmentForZipDTO.getFileName(), reportAttachmentForZipDTO.getCreatedAt()));
                zipOut.putNextEntry(entry);
                zipOut.write(reportAttachmentForZipDTO.getContent());
                zipOut.closeEntry();

            }
            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            log.warn("Некорректное формирования zip method getStreamingResponseBodyByReportID");
            return new byte[0];
        }

        // TODO : переделать для админа и менеджера для изменений см. репорт сервисы
    }
}

