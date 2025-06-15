package rice_monkey.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import rice_monkey.booking.domain.Booking;

public interface JpaBookingRepository extends JpaRepository<Booking, Long> {
}
