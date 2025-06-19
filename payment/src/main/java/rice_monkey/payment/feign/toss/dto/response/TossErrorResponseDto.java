package rice_monkey.payment.feign.toss.dto.response;

public record TossErrorResponseDto(
        String code,
        String message
) {
}
