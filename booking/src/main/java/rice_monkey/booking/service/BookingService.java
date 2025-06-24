package rice_monkey.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rice_monkey.booking.dao.BookingRepository;
import rice_monkey.booking.domain.Booking;
import rice_monkey.booking.domain.BookingState;
import rice_monkey.booking.dto.request.BookingReserveRequestDto;
import rice_monkey.booking.dto.response.BookingReserveResponseDto;
import rice_monkey.booking.dto.response.BookingResponseDto;
import rice_monkey.booking.exception.business.Authorization.UnauthorizedBookingAccessException;
import rice_monkey.booking.exception.business.booking.AlreadyBookedException;
import rice_monkey.booking.exception.business.booking.BookingNotFoundException;
import rice_monkey.booking.feign.listing.dto.ListingDto;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    @Transactional
    public BookingReserveResponseDto reserve(BookingReserveRequestDto dto, ListingDto listingDto, Long guestId) {
        if (bookingRepository.existsOverlap(dto.listingId(), dto.checkin(), dto.checkout()))
            throw new AlreadyBookedException(dto.listingId());

        Booking booking = Booking.of(dto, guestId, listingDto);
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

        booking.updateState(BookingState.CANCELED);
        bookingRepository.delete(booking);
    }

    @Transactional
    public void confirm(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        booking.updateState(BookingState.CONFIRMED);
        bookingRepository.save(booking);
    }

}
