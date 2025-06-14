package rice_monkey.booking.dto.request;

import java.time.LocalDate;

public record NewBookingDto(
        long listingId,
        LocalDate checkin,
        LocalDate checkout,
        int guestCount
) {
}
