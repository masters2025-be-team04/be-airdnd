package rice_monkey.booking.exception.booking;

import org.springframework.http.HttpStatus;
import rice_monkey.booking.exception.BusinessException;

public abstract class BookingException extends BusinessException {
    protected BookingException(String errorCode, String message, HttpStatus status) {
        super(errorCode, message, status);
    }
}
