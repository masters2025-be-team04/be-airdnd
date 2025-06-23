package rice_monkey.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import rice_monkey.booking.feign.listing.ListingClient;
import rice_monkey.booking.feign.listing.dto.ListingDto;
import rice_monkey.booking.feign.payment.PaymentClient;
import rice_monkey.booking.feign.payment.dto.PaymentCreateRequestDto;
import rice_monkey.booking.feign.payment.dto.PaymentCreateResponseDto;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingEventRepository bookingEventRepository;
    @Mock
    private ListingClient listingClient;
    @Mock
    private PaymentClient paymentClient;
    @Mock
    private RedisLockService redisLockService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("락 획득에 실패하면 AlreadyBookedException 발생")
    void reserve_whenLockNotAcquired_thenThrowsAlreadyBookedException() {
        // given
        BookingReserveRequestDto dto = new BookingReserveRequestDto(1L, LocalDate.of(2025, 7, 10), LocalDate.of(2025, 7, 14), 2);
        given(redisLockService.acquire(RedisConstant.LOCK_LISTING + dto.listingId())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> bookingService.reserve(dto, 100L))
                .isInstanceOf(AlreadyBookedException.class)
                .hasMessageContaining("already");
        verify(redisLockService).acquire(RedisConstant.LOCK_LISTING + dto.listingId());
    }

    @Test
    @DisplayName("숙소 상태가 PUBLISHED가 아니면 ListingUnavailableException 발생")
    void reserve_whenListingNotPublished_thenThrowsListingUnavailableException() {
        // given
        BookingReserveRequestDto dto = new BookingReserveRequestDto(2L, LocalDate.of(2025, 7, 10), LocalDate.of(2025, 7, 12), 1);
        given(redisLockService.acquire(anyString())).willReturn(true);
        ListingDto listingDto = mock(ListingDto.class);
        given(listingClient.find(dto.listingId())).willReturn(listingDto);
        given(listingDto.status()).willReturn("DRAFT");

        // when & then
        assertThatThrownBy(() -> bookingService.reserve(dto, 200L))
                .isInstanceOf(ListingUnavailableException.class)
                .hasMessageContaining("status");
        verify(redisLockService).release(anyString());
    }

    @Test
    @DisplayName("정상적으로 예약 시 Booking 저장, 이벤트 발행 및 결제 세션 요청")
    void reserve_success_andTriggersPaymentSession() {
        // given
        BookingReserveRequestDto dto = new BookingReserveRequestDto(
                3L,
                LocalDate.of(2025, 7, 10),
                LocalDate.of(2025, 7, 15),
                3
        );
        given(redisLockService.acquire(anyString())).willReturn(true);
        ListingDto listingDto = mock(ListingDto.class);
        given(listingClient.find(dto.listingId())).willReturn(listingDto);
        given(listingDto.status()).willReturn(BookingConstant.LISTING_PUBLISHED);
        given(listingDto.price()).willReturn(150);
        given(listingDto.name()).willReturn("Test Listing");

        given(bookingRepository.save(any(Booking.class))).willAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(10L);
            return b;
        });

        // 결제 세션 생성 스텁
        String expectedUrl = "https://checkout.pg.com/session/xyz123";
        given(paymentClient.createPayment(any(PaymentCreateRequestDto.class)))
                .willReturn(new PaymentCreateResponseDto(20L, expectedUrl));

        // when
        BookingReserveResponseDto response = bookingService.reserve(dto, 300L);

        // then
        // 1) 예약 저장 및 이벤트 발행
        verify(bookingRepository).save(any(Booking.class));
        verify(bookingEventRepository).save(any(BookingEvent.class));

        // 2) PaymentClient 호출
        verify(paymentClient).createPayment(argThat(req ->
                req.bookingId() == 10L &&
                        req.amount() == 150 &&
                        req.callbackUrl().equals("https://api.my-domain.com/api/payments/webhook")
        ));

        // 3) 응답 내 nextAction.checkoutUrl 값 검증
        assertThat(response.nextAction().checkoutUrl()).isEqualTo(expectedUrl);

        // 4) 락 해제
        verify(redisLockService).release(anyString());
    }

    @Test
    @DisplayName("예약 조회 시 존재하지 않으면 BookingNotFoundException 발생")
    void getBooking_whenNotFound_thenThrowsBookingNotFoundException() {
        // given
        given(bookingRepository.findById(100L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookingService.getBooking(100L, 1L))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    @DisplayName("예약 조회 시 본인의 예약이 아니면 UnauthorizedBookingAccessException 발생")
    void getBooking_whenUnauthorized_thenThrowsUnauthorizedBookingAccessException() {
        // given
        Booking booking = new Booking();
        booking.setId(200L);
        booking.setGuestId(50L);
        given(bookingRepository.findById(200L)).willReturn(Optional.of(booking));

        // when & then
        assertThatThrownBy(() -> bookingService.getBooking(200L, 99L))
                .isInstanceOf(UnauthorizedBookingAccessException.class);
    }

    @Test
    @DisplayName("예약 조회 성공 시 BookingResponseDto 반환")
    void getBooking_success_returnsDto() {
        // given
        Booking booking = Booking.builder()
                .listingId(5L)
                .guestId(500L)
                .checkinAt(LocalDate.of(2025, 6, 1))
                .checkoutAt(LocalDate.of(2025, 6, 5))
                .nights(4)
                .guestCount(2)
                .paymentAmount(200)
                .listingTitleSnapshot("Title")
                .build();
        booking.setId(300L);
        given(bookingRepository.findById(300L)).willReturn(Optional.of(booking));

        // when
        BookingResponseDto dto = bookingService.getBooking(300L, 500L);

        // then
        assertThat(dto.id()).isEqualTo(300L);
        assertThat(dto.state()).isEqualTo(BookingState.REQUESTED.toString());
    }

    @Test
    @DisplayName("예약 취소 시 상태가 CANCELED로 변경되고 이벤트 발행 및 삭제 호출")
    void cancelBooking_success_deletesBookingAndEmitsEvent() {
        // given
        Booking booking = spy(Booking.builder()
                .listingId(7L)
                .guestId(700L)
                .checkinAt(LocalDate.of(2025, 8, 1))
                .checkoutAt(LocalDate.of(2025, 8, 3))
                .nights(2)
                .guestCount(1)
                .paymentAmount(100)
                .listingTitleSnapshot("Title")
                .build());
        booking.setId(400L);
        given(bookingRepository.findById(400L)).willReturn(Optional.of(booking));

        // when
        bookingService.cancelBooking(400L, 700L);

        // then
        assertThat(booking.getStatus()).isEqualTo(BookingState.CANCELED);
        verify(bookingEventRepository).save(any(BookingEvent.class));
        verify(bookingRepository).delete(booking);
    }

    @Test
    @DisplayName("예약 확정 시 상태가 CONFIRMED로 변경되고 이벤트 발행")
    void confirm_success_setsStatusAndEmitsEvent() {
        // given
        Booking booking = spy(Booking.builder()
                .listingId(8L)
                .guestId(800L)
                .checkinAt(LocalDate.of(2025, 9, 1))
                .checkoutAt(LocalDate.of(2025, 9, 2))
                .nights(1)
                .guestCount(1)
                .paymentAmount(50)
                .listingTitleSnapshot("Title")
                .build());
        booking.setId(500L);
        given(bookingRepository.findById(500L)).willReturn(Optional.of(booking));

        // when
        bookingService.confirm(500L);

        // then
        assertThat(booking.getStatus()).isEqualTo(BookingState.CONFIRMED);
        verify(bookingEventRepository).save(any(BookingEvent.class));
    }
}
