package rice_monkey.booking.exception.booking;

import org.springframework.http.HttpStatus;

public class ListingUnavailableException extends BookingException {
    public ListingUnavailableException(Long listingId, String status) {
        super("LISTING_UNAVAILABLE",
                "Listing " + listingId + " status is '" + status + "', cannot book.",
                HttpStatus.BAD_REQUEST);
    }
}
