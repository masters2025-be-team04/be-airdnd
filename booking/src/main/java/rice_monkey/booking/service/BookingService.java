package rice_monkey.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rice_monkey.booking.common.constants.BookingConstant;
import rice_monkey.booking.common.constants.RedisConstant;
import rice_monkey.booking.dao.BookingEventRepository;
import rice_monkey.booking.dao.BookingRepository;
import rice_monkey.booking.domain.Booking;
import rice_monkey.booking.domain.BookingEvent;
import rice_monkey.booking.domain.BookingState;
import rice_monkey.booking.dto.request.BookingReserveRequestDto;
import rice_monkey.booking.dto.response.BookingReserveResponseDto;
import rice_monkey.booking.dto.response.BookingResponseDto;
import rice_monkey.booking.exception.booking.BookingNotFoundException;
import rice_monkey.booking.exception.booking.ListingUnavailableException;
import rice_monkey.booking.exception.Authorization.UnauthorizedBookingAccessException;
import rice_monkey.booking.exception.booking.AlreadyBookedException;
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
    public BookingReserveResponseDto reserve(BookingReserveRequestDto dto, Long guestId) throws JsonProcessingException {
        String key = RedisConstant.LOCK_LISTING + dto.listingId();
        if (!redisLockService.acquire(key)) {
            throw new AlreadyBookedException(dto.listingId());
        }

        try {
            ListingDto listing = listingClient.find(dto.listingId());
            if (!listing.status().equals(BookingConstant.LISTING_PUBLISHED)) {
                throw new ListingUnavailableException(dto.listingId(), listing.status());
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
            bookingEventRepository.save(BookingEvent.of(BookingConstant.BOOKING_REQUESTED, booking));

            return BookingReserveResponseDto.from(booking);
        } finally {
            redisLockService.release(key);
        }
    }

    @Transactional(readOnly = true)
    public BookingResponseDto getBooking(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (!booking.getGuestId().equals(userId)) {
            throw new UnauthorizedBookingAccessException(id, userId);
        }
        return BookingResponseDto.from(booking);
    }

    @Transactional
    public void cancelBooking(long id, long userId) throws JsonProcessingException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (!booking.getGuestId().equals(userId)) {
            throw new UnauthorizedBookingAccessException(id, userId);
        }

        booking.setStatus(BookingState.CANCELED);
        bookingEventRepository.save(BookingEvent.of(BookingConstant.BOOKING_CANCELED, booking));
        bookingRepository.delete(booking);
    }

    @Transactional
    public void confirm(Long id) throws JsonProcessingException {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        booking.setStatus(BookingState.CONFIRMED);
        bookingEventRepository.save(BookingEvent.of(BookingConstant.BOOKING_CONFIRMED, booking));
    }

}
