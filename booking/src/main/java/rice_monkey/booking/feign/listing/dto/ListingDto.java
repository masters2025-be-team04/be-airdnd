package rice_monkey.booking.feign.listing.dto;

public record ListingDto(
        Long id,
        String name,
        long price,
        String status
) {
}
