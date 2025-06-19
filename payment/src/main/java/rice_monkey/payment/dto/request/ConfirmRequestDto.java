package rice_monkey.payment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ConfirmRequestDto(

        @NotBlank
        String paymentKey,

        @NotBlank
        String orderId,

        @Min(100) // 최소 결제 금액 100원
        long amount,

        @Positive
        long bookingId

) {
}
