package rice_monkey.booking.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderIdGenerator {

    private OrderIdGenerator() {
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS");

    public static String generate(long listingId, long guestId) {
        String timestamp = FORMATTER.format(LocalDateTime.now());
        return String.format("%d-%d-%s", listingId, guestId, timestamp);
    }

}
