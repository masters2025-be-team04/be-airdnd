package rice_monkey.payment.feign.booking;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "booking",
        url = "http://localhost:8080"
)
public interface BookingClient {

    @PatchMapping("/internal/bookings/{id}/confirm")
    void confirm(@PathVariable long id);

    @PatchMapping("/internal/bookings/{id}/cancel")
    void cancel(@PathVariable long id);

}
