package rice_monkey.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rice_monkey.booking.domain.BookingEvent;

import java.util.List;

public interface JpaBookingEventRepository extends JpaRepository<BookingEvent, Long> {
    List<BookingEvent> findByPublishedFalse(Pageable pageable);
}
