package rice_monkey.booking.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "booking_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    @Id
    @GeneratedValue
    private Long id;
    private Long bookingId;
    private String eventType;
    @Column(columnDefinition = "json")
    private String payload;
    private boolean published = false;
    private LocalDateTime createdAt = LocalDateTime.now();

    public static BookingEvent of(String type, Booking b) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new BookingEvent(null,
                b.getId(),
                type,
                om.writeValueAsString(Map.of(
                        "listingId", b.getListingId(),
                        "guestId", b.getGuestId(),
                        "checkin", b.getCheckinAt(),
                        "checkout", b.getCheckoutAt())),
                false,
                LocalDateTime.now());
    }

}
