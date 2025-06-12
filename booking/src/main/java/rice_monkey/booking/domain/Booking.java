package rice_monkey.booking.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

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
    @CreationTimestamp
    private LocalDateTime createdAt;

}
