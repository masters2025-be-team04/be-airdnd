package rice_monkey.booking.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import rice_monkey.booking.feign.dto.ListingDto;

@FeignClient(name = "listing", url = "http://localhost:8081", path = "/api/listings")
public interface ListingClient {
    @GetMapping("/internal/listings/{id}")
    ListingDto find(@PathVariable Long id);
}
