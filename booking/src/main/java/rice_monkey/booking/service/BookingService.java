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
import rice_monkey.booking.exception.business.Authorization.UnauthorizedBookingAccessException;
import rice_monkey.booking.exception.business.booking.AlreadyBookedException;
import rice_monkey.booking.exception.business.booking.BookingNotFoundException;
import rice_monkey.booking.exception.business.booking.ListingUnavailableException;
import rice_monkey.booking.exception.infra.JsonSerializationException;
import rice_monkey.booking.feign.listing.ListingClient;
import rice_monkey.booking.feign.listing.dto.ListingDto;
import rice_monkey.booking.feign.payment.PaymentClient;
import rice_monkey.booking.feign.payment.dto.PaymentCreateRequestDto;
import rice_monkey.booking.feign.payment.dto.PaymentCreateResponseDto;

import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingEventRepository bookingEventRepository;
    private final ListingClient listingClient;
    private final PaymentClient paymentClient;
    private final RedisLockService redisLockService;

    @Transactional
    public BookingReserveResponseDto reserve(BookingReserveRequestDto dto, Long guestId) {
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

            BookingEvent bookingEvent = safeCreateBookingEvent(booking, BookingConstant.BOOKING_REQUESTED);
            bookingEventRepository.save(bookingEvent);

            // 결제 세션 생성
            String callbackUrl = "https://api.my-domain.com/api/payments/webhook";
            PaymentCreateRequestDto payReq = new PaymentCreateRequestDto(
                    booking.getId(),
                    booking.getPaymentAmount(),
                    callbackUrl
            );
            PaymentCreateResponseDto payRes = paymentClient.createPayment(payReq);

            return BookingReserveResponseDto.from(booking, payRes.checkoutUrl());
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
    public void cancelBooking(long id, long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (!booking.getGuestId().equals(userId)) {
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
