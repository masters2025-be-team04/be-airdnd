package rice_monkey.booking.dto.response;

import rice_monkey.booking.domain.Booking;

public record BookingReserveResponseDto(
        long id,
        String orderId,
        long amount,
        String clientKey
) {

    public static BookingReserveResponseDto from(Booking booking, String orderId, String clientKey) {
        return new BookingReserveResponseDto(
                booking.getId(),
                orderId,
                booking.getPaymentAmount(),
                clientKey
        );
    }

}
