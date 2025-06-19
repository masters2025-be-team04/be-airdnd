package rice_monkey.booking.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import rice_monkey.booking.dto.request.BookingReserveRequestDto;
import rice_monkey.booking.dto.response.BookingReserveResponseDto;

@Service
@RequiredArgsConstructor
public class BookingReserveService {

    private final BookingService bookingService;
    private final RedisLockService redisLockService;

    /**
     * this is to separate the transaction from the lock.
     */
    public BookingReserveResponseDto reserve(BookingReserveRequestDto dto, Long guestId) {
        RLock lock = redisLockService.bookingLock(dto.listingId(), dto.checkin(), dto.checkout());

        return redisLockService.lockAndExecute(lock, () -> bookingService.reserve(dto, guestId));
    }

}
