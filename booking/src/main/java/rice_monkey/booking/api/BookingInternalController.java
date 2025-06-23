package rice_monkey.booking.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rice_monkey.booking.service.BookingService;

@RestController
@RequestMapping("/internal/bookings")
@RequiredArgsConstructor
class BookingInternalController {

    private final BookingService bookingService;

    @PatchMapping("/{id}/confirm")
    public void confirm(@PathVariable Long id) throws Exception {
        bookingService.confirm(id);
    }

}
