package rice_monkey.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rice_monkey.booking.domain.Booking;

import java.time.LocalDate;

public interface JpaBookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
              SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
              FROM Booking b
              WHERE b.listingId = :listingId
                AND b.checkinAt  < :checkOut
                AND b.checkoutAt > :checkIn
                AND b.state    <> 'CANCELLED'
            """)
    boolean existsOverlap(long listingId, LocalDate checkIn, LocalDate checkOut);

}
