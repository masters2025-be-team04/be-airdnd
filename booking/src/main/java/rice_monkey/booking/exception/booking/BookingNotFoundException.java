package rice_monkey.booking.exception.booking;

import org.springframework.http.HttpStatus;

public class BookingNotFoundException extends BookingException {
    public BookingNotFoundException(Long bookingId) {
        super("BOOKING_NOT_FOUND",
                "Booking with id " + bookingId + " not found.",
                HttpStatus.NOT_FOUND);
    }
}
