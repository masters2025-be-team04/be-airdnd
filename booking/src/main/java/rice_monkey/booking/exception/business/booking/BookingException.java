package rice_monkey.booking.exception.business.booking;

import org.springframework.http.HttpStatus;
import rice_monkey.booking.exception.business.BusinessException;

public abstract class BookingException extends BusinessException {
    protected BookingException(String errorCode, String message, HttpStatus status) {
        super(errorCode, message, status);
    }
}
