package rice_monkey.payment.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rice_monkey.payment.dto.request.ConfirmRequestDto;
import rice_monkey.payment.dto.response.ConfirmResponseDto;
import rice_monkey.payment.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmResponseDto> confirm(
            @RequestBody @Valid ConfirmRequestDto dto
    ) {
        return ResponseEntity.ok(paymentService.confirm(dto));
    }

}
