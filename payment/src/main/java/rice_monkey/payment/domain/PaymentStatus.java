package rice_monkey.payment.domain;

public enum PaymentStatus {
    READY,
    IN_PROGRESS,
    EXPIRED,
    DONE,
    ABORTED,
    CANCELED,
    PARTIAL_CANCELED
}
