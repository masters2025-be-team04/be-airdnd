package rice_monkey.booking.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import rice_monkey.booking.dto.request.BookingReserveRequestDto;
import rice_monkey.booking.dto.response.BookingReserveResponseDto;
import rice_monkey.booking.dto.response.BookingResponseDto;
import rice_monkey.booking.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto getBooking(@PathVariable Long id,
                                         @RequestHeader("X-User-Id") Long userId) {
        return bookingService.getBooking(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingReserveResponseDto reserve(@Valid @RequestBody BookingReserveRequestDto req,
                                             @RequestHeader("X-User-Id") Long userId
    ) {
        return bookingService.reserve(req, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id,
                       @RequestHeader("X-User-Id") Long userId
    ) {
        bookingService.cancelBooking(id, userId);
    }

}
