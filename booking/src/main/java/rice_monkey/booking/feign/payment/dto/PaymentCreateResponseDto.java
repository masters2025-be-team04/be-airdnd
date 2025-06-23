package rice_monkey.booking.feign.payment.dto;

public record PaymentCreateResponseDto(
        long paymentId,
        String checkoutUrl // URL to redirect the user to complete the payment
) {
}
