package rice_monkey.payment.feign.toss.dto.request;

import lombok.Builder;

@Builder
public record TossConfirmRequestDto(
        String paymentKey,
        String orderId,
        long amount
) {
}
