package rice_monkey.booking.exception.business.booking;

import org.springframework.http.HttpStatus;

public class AlreadyBookedException extends BookingException {
    public AlreadyBookedException(Long listingId) {
        super("BOOKING_ALREADY_IN_PROGRESS",
                "Listing " + listingId + " is already being booked.",
                HttpStatus.CONFLICT);
    }

}
