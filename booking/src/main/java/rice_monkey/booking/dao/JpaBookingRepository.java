package rice_monkey.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import rice_monkey.booking.domain.Booking;

import java.util.Optional;

public interface JpaBookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByIdAndGuestId(Long id, Long guestId);
}
