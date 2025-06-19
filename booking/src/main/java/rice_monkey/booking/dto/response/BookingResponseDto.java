package rice_monkey.booking.dto.response;

import rice_monkey.booking.domain.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponseDto(
        long id,
        long listingId,
        String state,
        LocalDate checkin,
        LocalDate checkout,
        int guestCount,
        String listingTitleSnapshot,
        long paymentAmount,
        LocalDateTime createdAt
) {

    public static BookingResponseDto from(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getListingId(),
                booking.getState().name(),
                booking.getCheckinAt(),
                booking.getCheckoutAt(),
                booking.getGuestCount(),
                booking.getListingTitleSnapshot(),
                booking.getPaymentAmount(),
                booking.getCreatedAt()
        );
    }

}
