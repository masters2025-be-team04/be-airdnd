package rice_monkey.booking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("예약 생성 시: 이미 예약이 겹치면 예외를 반환한다")
    void reserve_alreadyBooked_throwsException() {
        // given: 중복 예약이 있다고 설정
        BookingReserveRequestDto dto = new BookingReserveRequestDto(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                2
        );
        given(bookingRepository.existsOverlap(dto.listingId(), dto.checkin(), dto.checkout()))
                .willReturn(true);

        // when: 예약을 시도하면
        // then: AlreadyBookedException 발생
        assertThatThrownBy(() -> bookingService.reserve(dto, mock(ListingDto.class), 100L))
                .isInstanceOf(AlreadyBookedException.class)
                .hasMessageContaining("Listing 1 is already being booked.");
        then(bookingRepository).should().existsOverlap(dto.listingId(), dto.checkin(), dto.checkout());
        then(bookingRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("예약 생성 시: 유효한 요청이면 저장 후 응답 DTO 반환")
    void reserve_success_savesAndReturnsDto() {
        // given: 중복 예약 없음, 저장될 Booking 준비
        long listingId = 2L;
        LocalDate checkin = LocalDate.now();
        LocalDate checkout = checkin.plusDays(2);
        int guestCount = 3;
        BookingReserveRequestDto dto = new BookingReserveRequestDto(
                listingId,
                checkin,
                checkout,
                guestCount
        );
        long guestId = 123L;
        ListingDto listingDto = mock(ListingDto.class);
        given(bookingRepository.existsOverlap(listingId, checkin, checkout)).willReturn(false);
        Booking savedBooking = mock(Booking.class);
        given(savedBooking.getId()).willReturn(55L);
        given(bookingRepository.save(any(Booking.class))).willReturn(savedBooking);

        // when: 예약을 요청하면
        BookingReserveResponseDto response = bookingService.reserve(dto, listingDto, guestId);

        // then: 저장된 ID로 응답 DTO가 생성된다
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(55L);
        then(bookingRepository).should().save(any(Booking.class));
    }

    @Test
    @DisplayName("예약 조회 시: 존재하고 권한이 있으면 DTO 반환")
    void getBooking_whenFoundAndAuthorized_returnsDto() {
        // given: 조회 대상이 존재하고, guestId가 동일
        long id = 10L;
        long userId = 200L;
        Booking mockBooking = mock(Booking.class);
        given(mockBooking.getGuestId()).willReturn(userId);
        given(mockBooking.getState()).willReturn(BookingState.REQUESTED);
        given(bookingRepository.findById(id)).willReturn(Optional.of(mockBooking));

        // when: 예약을 조회하면
        BookingResponseDto result = bookingService.getBooking(id, userId);

        // then: 예약 정보가 DTO로 반환된다
        assertThat(result).isNotNull();
        then(bookingRepository).should().findById(id);
    }

    @Test
    @DisplayName("예약 조회 시: 존재하지 않으면 BookingNotFoundException 발생")
    void getBooking_whenNotFound_throwsException() {
        // given: 조회 대상이 없음
        given(bookingRepository.findById(5L)).willReturn(Optional.empty());

        // when & then: 예외 발생
        assertThatThrownBy(() -> bookingService.getBooking(5L, 1L))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("Booking with id 5 not found");
    }

    @Test
    @DisplayName("예약 조회 시: 권한이 없으면 UnauthorizedBookingAccessException 발생")
    void getBooking_whenUnauthorized_throwsException() {
        // given: 예약이 존재하나 guestId 불일치
        long id = 20L;
        Booking mockBooking = mock(Booking.class);
        given(mockBooking.getGuestId()).willReturn(999L);
        given(bookingRepository.findById(id)).willReturn(Optional.of(mockBooking));

        // when & then: 권한 예외 발생
        assertThatThrownBy(() -> bookingService.getBooking(id, 100L))
                .isInstanceOf(UnauthorizedBookingAccessException.class)
                .hasMessageContaining("User 100 cannot access booking 20.");
    }

    @Test
    @DisplayName("예약 취소 시: 정상 권한이면 상태 변경 후 삭제 호출")
    void cancelBooking_success_callsDelete() {
        // given: 예약이 존재하고 guestId 동일
        long id = 30L;
        long userId = 300L;
        Booking mockBooking = mock(Booking.class);
        given(mockBooking.getGuestId()).willReturn(userId);
        given(bookingRepository.findById(id)).willReturn(Optional.of(mockBooking));

        // when: 예약 취소를 요청하면
        bookingService.cancelBooking(id, userId);

        // then: 상태가 CANCELED로, 리포지토리 삭제 호출
        then(mockBooking).should().updateState(BookingState.CANCELED);
        then(bookingRepository).should().delete(mockBooking);
    }

    @Test
    @DisplayName("예약 취소 시: 존재하지 않으면 BookingNotFoundException 발생")
    void cancelBooking_whenNotFound_throwsException() {
        // given: 대상 없음
        given(bookingRepository.findById(99L)).willReturn(Optional.empty());

        // when & then: 예외 발생
        assertThatThrownBy(() -> bookingService.cancelBooking(99L, 1L))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    @DisplayName("예약 취소 시: 권한이 없으면 UnauthorizedBookingAccessException 발생")
    void cancelBooking_whenUnauthorized_throwsException() {
        // given: 예약이 존재하나 guestId 불일치
        long id = 40L;
        Booking mockBooking = mock(Booking.class);
        given(mockBooking.getGuestId()).willReturn(1234L);
        given(bookingRepository.findById(id)).willReturn(Optional.of(mockBooking));

        // when & then: 권한 예외 발생
        assertThatThrownBy(() -> bookingService.cancelBooking(id, 1L))
                .isInstanceOf(UnauthorizedBookingAccessException.class);
    }

    @Test
    @DisplayName("예약 확정 시: 상태 변경 후 저장 호출")
    void confirm_success_updatesAndSaves() {
        // given: 예약이 존재함
        long id = 50L;
        Booking mockBooking = mock(Booking.class);
        given(bookingRepository.findById(id)).willReturn(Optional.of(mockBooking));

        // when: 예약 확정을 요청하면
        bookingService.confirm(id);

        // then: 상태가 CONFIRMED로 update, 저장 호출
        then(mockBooking).should().updateState(BookingState.CONFIRMED);
        then(bookingRepository).should().save(mockBooking);
    }

    @Test
    @DisplayName("예약 확정 시: 존재하지 않으면 BookingNotFoundException 발생")
    void confirm_whenNotFound_throwsException() {
        // given: 대상 없음
        given(bookingRepository.findById(123L)).willReturn(Optional.empty());

        // when & then: 예외 발생
        assertThatThrownBy(() -> bookingService.confirm(123L))
                .isInstanceOf(BookingNotFoundException.class);
    }
}
