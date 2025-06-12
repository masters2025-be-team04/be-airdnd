package rice_monkey.booking.feign.dto;

public record ListingDto(
        Long id,
        String name,
        int price,
        String status
) {
}
