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

    /**
     * 클라이언트가 실제 결제 페이지로 이동할 고유 URL을 포함합니다.
     * type 필드는 프론트에서 어떤 행동을 해야 할지(예: REDIRECT_TO_PAYMENT) 구분할 때 씁니다.
     */
    public record NextAction(
            String type,
            String checkoutUrl
    ) {
    }

    public static BookingReserveResponseDto from(Booking booking, String checkoutUrl) {
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
                new NextAction(
                        "REDIRECT_TO_PAYMENT",
                        checkoutUrl
                ),
                "booking." + booking.getId()
        );
    }

}
