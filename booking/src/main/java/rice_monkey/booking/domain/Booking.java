package rice_monkey.booking.domain;

import jakarta.persistence.*;
import lombok.*;
import rice_monkey.booking.common.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "listing_id", nullable = false)
    private Long listingId;

    @Column(name = "guest_id", nullable = false)
    private Long guestId;

    @Column(name = "checkin_at", nullable = false, columnDefinition = "DATE")
    private LocalDate checkinAt;

    @Column(name = "checkout_at", nullable = false, columnDefinition = "DATE")
    private LocalDate checkoutAt;

    @Column(name = "nights", nullable = false)
    private Integer nights;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(name = "payment_amount", nullable = false)
    private Integer paymentAmount;

    @Column(name = "listing_title_snapshot", nullable = false)
    private String listingTitleSnapshot;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingState status = BookingState.REQUESTED;

}
