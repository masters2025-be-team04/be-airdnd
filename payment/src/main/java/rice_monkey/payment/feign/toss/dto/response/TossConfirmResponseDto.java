package rice_monkey.payment.feign.toss.dto.response;

public record TossConfirmResponseDto(
        String paymentKey,
        String orderId,
        String status,
        String orderName,
        long totalAmount,
        String approvedAt,
        Receipt receipt
) {

    public record Receipt(
            String url
    ) {
    }

}
