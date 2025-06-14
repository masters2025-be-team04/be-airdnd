package rice_monkey.booking.dto.response;

import rice_monkey.booking.domain.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingReserveResponseDto(
        long id,
        long listingId,
        String state,
        LocalDate checkin,
        LocalDate checkout,
        int guestCount,
        String listingTitleSnapshot,
        int paymentAmount,
        LocalDateTime createdAt,
        NextAction nextAction,
        String wsTopic
) {

    private record NextAction(
            String type,
            String paymentUrl
    ) {
    }

    public static BookingReserveResponseDto from(Booking booking) {
        return new BookingReserveResponseDto(
                booking.getId(),
                booking.getListingId(),
                booking.getStatus().name(),
                booking.getCheckinAt(),
                booking.getCheckoutAt(),
                booking.getGuestCount(),
                booking.getListingTitleSnapshot(),
                booking.getPaymentAmount(),
                booking.getCreatedAt(),
                new NextAction("PAYMENT_REQUIRED",
                        "payments/" + booking.getId()),
                "booking." + booking.getId()
        );
    }

}
