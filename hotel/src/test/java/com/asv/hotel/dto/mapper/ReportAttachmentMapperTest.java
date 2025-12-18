package com.asv.hotel.dto.mapper;

import com.asv.hotel.dto.reportattachmendto.ReportAttachmentDTO;
import com.asv.hotel.entities.ReportAttachment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportAttachmentMapperTest {

    @Mock
    private MultipartFile mockMultipartFile;

    @Test
    void shouldMapReportAttachmentToReportAttachmentDTO() {

        LocalDateTime now = LocalDateTime.now();
        ReportAttachment attachment = ReportAttachment.builder()
                .id(1L)
                .fileName("test.png")
                .contentType("application/png")
                .size(1024L)
                .createdAt(now)
                .build();

        ReportAttachmentDTO dto = ReportAttachmentMapper.INSTANCE.reportAttachmentToReportAttachmentDTO(attachment);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFileName()).isEqualTo("test.png");
        assertThat(dto.getContentType()).isEqualTo("application/png");
        assertThat(dto.getSize()).isEqualTo(1024L);
        assertThat(dto.getCreatedAt()).isEqualTo(now.toLocalDate());
    }

    @Test
    void shouldMapMultipartFileToReportAttachmentWithoutType() throws IOException {
        String name = "file";
        String originalFilename = "document.png";
        String contentType = "text/plain";
        byte[] content = "Hello, world!".getBytes();
        long size = content.length;

        MultipartFile multipartFile = new MockMultipartFile(
                name,
                originalFilename,
                contentType,
                content
        );

        ReportAttachment attachment = ReportAttachmentMapper.INSTANCE.multipartFileToReportAttachmentWithoutType(multipartFile);

        assertThat(attachment).isNotNull();
        assertThat(attachment.getFileName()).isEqualTo(name);
        assertThat(attachment.getSize()).isEqualTo(size);
        assertThat(attachment.getContent()).isEqualTo(content);
    }

    @Test
    void shouldConvertLocalDateTimeToLocalDate() {

        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 18, 14, 30);
        LocalDate expectedDate = LocalDate.of(2025, 10, 18);

        LocalDate result = ReportAttachmentMapper.INSTANCE.localDateTimeToLocalDate(dateTime);

        assertThat(result).isEqualTo(expectedDate);
    }

    @Test
    void shouldGetMultipartFileName() {

        String expectedName = "uploadField";
        when(mockMultipartFile.getName()).thenReturn(expectedName);

        String result = ReportAttachmentMapper.INSTANCE.multiPartFileGetName(mockMultipartFile);

        assertThat(result).isEqualTo(expectedName);
    }

}