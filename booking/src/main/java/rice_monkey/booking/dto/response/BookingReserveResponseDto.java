package rice_monkey.booking.dto.response;

import rice_monkey.booking.domain.Booking;

public record BookingReserveResponseDto(
        long id,
        String orderId,
        long amount
) {

    public static BookingReserveResponseDto from(Booking booking) {
        return new BookingReserveResponseDto(
                booking.getId(),
                booking.getOrderId(),
                booking.getPaymentAmount()
        );
    }

}
