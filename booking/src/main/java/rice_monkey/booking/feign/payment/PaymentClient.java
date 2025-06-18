package rice_monkey.booking.feign.payment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rice_monkey.booking.feign.payment.dto.PaymentCreateRequestDto;
import rice_monkey.booking.feign.payment.dto.PaymentCreateResponseDto;

// @todo Update the URL to the actual payment service URL when deploying
@FeignClient(name = "payment" , url = "http://localhost:8082", path = "/api/payments")
public interface PaymentClient {
    @PostMapping("/internal/payments")
    PaymentCreateResponseDto createPayment(@RequestBody PaymentCreateRequestDto req);
}
