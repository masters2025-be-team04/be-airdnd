package rice_monkey.payment.domain;

import jakarta.persistence.*;
import lombok.*;
import rice_monkey.payment.feign.toss.dto.response.TossConfirmResponseDto;

import java.time.OffsetDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_id", nullable = false)
    private long bookingId;

    @Column(name = "order_id", length = 64, nullable = false, unique = true)
    private String orderId;

    @Column(name = "payment_key", length = 200, nullable = false, unique = true)
    private String paymentKey;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "approved_at", nullable = false)
    private OffsetDateTime approvedAt;

    @Column(name = "receipt_url")
    private String receiptUrl;

    public static Payment from(TossConfirmResponseDto dto, long bookingId) {
        return Payment.builder()
                .bookingId(bookingId)
                .orderId(dto.orderId())
                .paymentKey(dto.paymentKey())
                .amount(dto.totalAmount())
                .orderName(dto.orderName())
                .status(PaymentStatus.valueOf(dto.status().toUpperCase()))
                .approvedAt(OffsetDateTime.parse(dto.approvedAt()))
                .receiptUrl(dto.receipt().url())
                .build();
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

}
