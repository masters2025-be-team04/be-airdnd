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
    private Long listingId;
    private Long guestId;
    private LocalDate checkinAt;
    private LocalDate checkoutAt;
    private Integer nights;
    private Integer guestCount;
    private Integer paymentAmount;
    private String listingTitleSnapshot;
    @Enumerated(EnumType.STRING)
    private BookingState status = BookingState.REQUESTED;

}
