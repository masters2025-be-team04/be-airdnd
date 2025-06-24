package rice_monkey.payment.feign.toss;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rice_monkey.payment.config.TossFeignConfig;
import rice_monkey.payment.feign.toss.dto.request.TossCancelRequestDto;
import rice_monkey.payment.feign.toss.dto.request.TossConfirmRequestDto;
import rice_monkey.payment.feign.toss.dto.response.TossConfirmResponseDto;

@FeignClient(
        name = "toss",
        url = "https://api.tosspayments.com",
        configuration = TossFeignConfig.class
)
public interface TossPaymentClient {

    @PostMapping(
            value = "/v1/payments/confirm",
            consumes = "application/json",
            produces = "application/json"
    )
    TossConfirmResponseDto confirm(@RequestBody TossConfirmRequestDto body);

    @PostMapping(
            value = "/v1/payments/{paymentKey}/cancel",
            consumes = "application/json",
            produces = "application/json"
    )
    TossConfirmResponseDto cancel(@PathVariable String paymentKey,
                                  @RequestBody TossCancelRequestDto body);

}
