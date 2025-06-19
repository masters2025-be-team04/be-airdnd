package rice_monkey.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rice_monkey.booking.common.constants.BookingConstant;
import rice_monkey.booking.dao.BookingRepository;
import rice_monkey.booking.domain.Booking;
import rice_monkey.booking.domain.BookingState;
import rice_monkey.booking.dto.request.BookingReserveRequestDto;
import rice_monkey.booking.dto.response.BookingReserveResponseDto;
import rice_monkey.booking.dto.response.BookingResponseDto;
import rice_monkey.booking.exception.business.Authorization.UnauthorizedBookingAccessException;
import rice_monkey.booking.exception.business.booking.AlreadyBookedException;
import rice_monkey.booking.exception.business.booking.BookingNotFoundException;
import rice_monkey.booking.exception.infra.JsonSerializationException;
import rice_monkey.booking.feign.listing.ListingClient;
import rice_monkey.booking.feign.listing.dto.ListingDto;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingEventRepository bookingEventRepository;
    private final ListingClient listingClient;
    private final RedisLockService redisLockService;

    @Transactional
    public BookingReserveResponseDto reserve(BookingReserveRequestDto dto, Long guestId) {
        if (bookingRepository.existsOverlap(dto.listingId(), dto.checkin(), dto.checkout()))
            throw new AlreadyBookedException(dto.listingId());

        ListingDto listing = listingClient.find(dto.listingId());
        Booking booking = Booking.of(dto, guestId, listing);
        Booking newBooking = bookingRepository.save(booking);

        return BookingReserveResponseDto.from(newBooking);
    }

    @Transactional(readOnly = true)
    public BookingResponseDto getBooking(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (booking.getGuestId() != userId) {
            throw new UnauthorizedBookingAccessException(id, userId);
        }
        return BookingResponseDto.from(booking);
    }

    @Transactional
    public void cancelBooking(long id, long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (booking.getGuestId() != userId) {
            throw new UnauthorizedBookingAccessException(id, userId);
        }

        booking.setStatus(BookingState.CANCELED);

        BookingEvent bookingEvent = safeCreateBookingEvent(booking, BookingConstant.BOOKING_CANCELED);
        bookingEventRepository.save(bookingEvent);

        bookingRepository.delete(booking);
    }

    @Transactional
    public void confirm(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        booking.setStatus(BookingState.CONFIRMED);
        BookingEvent bookingEvent = safeCreateBookingEvent(booking, BookingConstant.BOOKING_CONFIRMED);

        bookingEventRepository.save(bookingEvent);
    }

    private BookingEvent safeCreateBookingEvent(Booking booking, String eventType) {
        try {
            return BookingEvent.of(eventType, booking);
        } catch (JsonProcessingException e) {
            throw new JsonSerializationException("Error serializing booking event data");
        }
    }

}
