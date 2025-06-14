package rice_monkey.booking.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rice_monkey.booking.dto.request.NewBookingDto;
import rice_monkey.booking.dto.response.BookingDto;
import rice_monkey.booking.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto reserve(@Valid @RequestBody NewBookingDto req,
                              @RequestHeader("X-User-Id") Long userId
    ) throws Exception {
        return bookingService.reserve(req, userId);
    }

}
