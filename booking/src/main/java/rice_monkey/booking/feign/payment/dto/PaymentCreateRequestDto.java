package rice_monkey.booking.feign.payment.dto;

public record PaymentCreateRequestDto(
        long bookingId,
        int amount,
        String callbackUrl // URL to redirect the user after payment completion
) {
}
