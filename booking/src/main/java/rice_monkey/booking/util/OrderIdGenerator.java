package rice_monkey.booking.util;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class OrderIdGenerator {

    private static final char[] B62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private OrderIdGenerator() {
    }

    /**
     * Generates a unique order ID based on the listing ID and user ID.
     * The order ID is a combination of the current date in YYYYMMDD format
     * and a base-62 encoded value derived from the listing ID and user ID.
     *
     * @param listingId the listing ID
     * @param userId    the user ID
     * @return a unique order ID string
     */
    public static String generate(long listingId, long userId) {
        String date = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.BASIC_ISO_DATE);
        return date + encode62(listingId ^ (userId << 32));
    }

    private static String encode62(long v) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(B62[(int) (v % 62)]);
            v /= 62;
        } while (v > 0);
        return sb.reverse().toString();
    }

}
