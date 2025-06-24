package rice_monkey.payment.feign.toss.dto.request;

import rice_monkey.payment.dto.request.ConfirmRequestDto;

public record TossConfirmRequestDto(
        String paymentKey,
        String orderId,
        long amount
) {

    public static TossConfirmRequestDto from(ConfirmRequestDto dto) {
        return new TossConfirmRequestDto(dto.paymentKey(), dto.orderId(), dto.amount());
    }

}
