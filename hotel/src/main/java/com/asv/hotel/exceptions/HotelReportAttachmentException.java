package com.asv.hotel.exceptions;

import com.asv.hotel.dto.ErrorMessage;
import org.springframework.http.HttpStatus;

public class HotelReportAttachmentException extends HotelMainException {

    public HotelReportAttachmentException(String message) {
        super(new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR,message));
    }

    public HotelReportAttachmentException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
