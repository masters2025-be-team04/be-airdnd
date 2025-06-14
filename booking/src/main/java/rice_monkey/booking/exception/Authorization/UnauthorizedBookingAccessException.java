package rice_monkey.booking.exception.Authorization;


import org.springframework.http.HttpStatus;
import rice_monkey.booking.exception.BusinessException;

public class UnauthorizedBookingAccessException extends BusinessException {
    public UnauthorizedBookingAccessException(Long bookingId, Long userId) {
        super("BOOKING_ACCESS_DENIED",
                "User " + userId + " cannot access booking " + bookingId + ".",
                HttpStatus.FORBIDDEN);
    }
}
