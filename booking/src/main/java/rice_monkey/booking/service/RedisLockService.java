package rice_monkey.booking.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final RedissonClient redisson;
    private static final String KEY_FMT = "lock:booking:%d:%s:%s";

    public RLock bookingLock(Long listingId, LocalDate in, LocalDate out) {
        String key = KEY_FMT.formatted(
                listingId,
                in.format(DateTimeFormatter.BASIC_ISO_DATE),   // yyyyMMdd
                out.format(DateTimeFormatter.BASIC_ISO_DATE));
        return redisson.getLock(key);
    }

}
