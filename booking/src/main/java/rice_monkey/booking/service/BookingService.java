package rice_monkey.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rice_monkey.booking.dao.BookingEventRepository;
import rice_monkey.booking.dao.BookingRepository;
import rice_monkey.booking.domain.Booking;
import rice_monkey.booking.domain.BookingEvent;
import rice_monkey.booking.domain.BookingState;
import rice_monkey.booking.dto.request.NewBookingDto;
import rice_monkey.booking.dto.response.BookingDto;
import rice_monkey.booking.feign.ListingClient;
import rice_monkey.booking.feign.dto.ListingDto;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingEventRepository bookingEventRepository;
    private final ListingClient listingClient;
    private final RedisLockService redisLockService;

    @Transactional
    public BookingDto reserve(NewBookingDto dto, Long guestId) throws JsonProcessingException {
        String key = "lock:listing:" + dto.listingId();
        if (!redisLockService.acquire(key)) {
            throw new IllegalStateException("already booked");
        }

        try {
            ListingDto listing = listingClient.find(dto.listingId());
            if (!listing.status().equals("PUBLISHED")) {
                throw new IllegalStateException("listing is not available");
            }

            Booking booking = Booking.builder()
                    .listingId(dto.listingId())
                    .guestId(guestId)
                    .checkinAt(dto.checkin())
                    .checkoutAt(dto.checkout())
                    .nights((int) ChronoUnit.DAYS.between(dto.checkin(), dto.checkout()))
                    .guestCount(dto.guestCount())
                    .paymentAmount(listing.price())
                    .listingTitleSnapshot(listing.name())
                    .build();
            bookingRepository.save(booking);
            bookingEventRepository.save(BookingEvent.of("BOOKING_REQUESTED", booking));

            return BookingDto.from(booking);
        } finally {
            redisLockService.release(key);
        }
    }

    @Transactional
    public void confirm(Long id) throws JsonProcessingException {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.setStatus(BookingState.CONFIRMED);
        bookingEventRepository.save(BookingEvent.of("BOOKING_CONFIRMED", booking));
    }

}
