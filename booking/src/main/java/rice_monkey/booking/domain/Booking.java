package rice_monkey.booking.domain;

import jakarta.persistence.*;
import lombok.*;
import rice_monkey.booking.common.entity.BaseEntity;
import rice_monkey.booking.dto.request.BookingReserveRequestDto;
import rice_monkey.booking.feign.listing.dto.ListingDto;
import rice_monkey.booking.util.OrderIdGenerator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "listing_id", nullable = false)
    private long listingId;

    @Column(name = "guest_id", nullable = false)
    private long guestId;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "checkin_at", nullable = false, columnDefinition = "DATE")
    private LocalDate checkinAt;

    @Column(name = "checkout_at", nullable = false, columnDefinition = "DATE")
    private LocalDate checkoutAt;

    @Column(name = "stay_days", nullable = false)
    private int stayDays;

    @Column(name = "guest_count", nullable = false)
    private int guestCount;

    @Column(name = "payment_amount", nullable = false)
    private long paymentAmount;

    @Column(name = "listing_title_snapshot", nullable = false)
    private String listingTitleSnapshot;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private BookingState state = BookingState.REQUESTED;

    public static Booking of(BookingReserveRequestDto dto, long guestId, ListingDto listing) {
        int stayDays = (int) ChronoUnit.DAYS.between(dto.checkin(), dto.checkout());

        return Booking.builder()
                .listingId(dto.listingId())
                .guestId(guestId)
                .orderId(OrderIdGenerator.generate(dto.listingId(), guestId))
                .checkinAt(dto.checkin())
                .checkoutAt(dto.checkout())
                .stayDays(stayDays)
                .guestCount(dto.guestCount())
                .paymentAmount(listing.price() * stayDays * dto.guestCount())
                .listingTitleSnapshot(listing.name())
                .build();
    }

}
