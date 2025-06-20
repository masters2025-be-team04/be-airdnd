package rice_monkey.booking.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisBusyException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private static final Duration LOCK_WAIT = Duration.ofSeconds(2);
    private static final long LEASE_TIME = -1L;

    private final RedissonClient redisson;

    /**
     * Creates a multi-lock for the booking dates of a listing.
     * The lock is created for each date from checkin to checkout (inclusive).
     *
     * @param listingId the ID of the listing
     * @param checkin   the check-in date
     * @param checkout  the check-out date
     * @return a multi-lock that can be used to lock the booking dates
     */
    public RLock bookingLock(Long listingId, LocalDate checkin, LocalDate checkout) {
        List<RLock> dateLocks = checkin
                .datesUntil(checkout.plusDays(1))
                .map(date -> redisson.getLock(
                        "lock:booking:" + listingId + ":" +
                                date.format(DateTimeFormatter.BASIC_ISO_DATE)))
                .toList();

        return redisson.getMultiLock(dateLocks.toArray(new RLock[0]));
    }

    /**
     * Acquires the lock and executes the given action.
     * If the lock cannot be acquired, it throws a RedisBusyException.
     *
     * @param multiLock the multi-lock to acquire
     * @param action    the action to execute while holding the lock
     * @param <T>       the type of the result of the action
     * @return the result of the action
     */
    public <T> T lockAndExecute(RLock multiLock, Supplier<T> action) {
        boolean acquired;
        try {
            acquired = multiLock.tryLock(
                    LOCK_WAIT.toMillis(),
                    LEASE_TIME,
                    TimeUnit.MILLISECONDS
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RedisBusyException("예약 처리 대기 중 쓰레드가 중단되었습니다. 다시 시도해 주세요.");
        }
        if (!acquired) {
            throw new RedisBusyException("다른 사용자가 예약 중입니다. 잠시 후 다시 시도해 주세요.");
        }

        try {
            return action.get();
        } finally {
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
            }
        }
    }

}
