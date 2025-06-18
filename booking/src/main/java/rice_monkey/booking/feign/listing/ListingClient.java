package rice_monkey.booking.feign.listing;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import rice_monkey.booking.feign.listing.dto.ListingDto;

// @todo Update the URL to the actual listing service URL when deploying
@FeignClient(name = "listing", url = "http://localhost:8081", path = "/api/listings")
public interface ListingClient {
    @GetMapping("/internal/listings/{id}")
    ListingDto find(@PathVariable Long id);
}
