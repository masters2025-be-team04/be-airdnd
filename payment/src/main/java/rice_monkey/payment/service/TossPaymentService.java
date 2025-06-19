package rice_monkey.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rice_monkey.payment.dao.PaymentRepository;
import rice_monkey.payment.domain.Payment;
import rice_monkey.payment.dto.request.ConfirmRequestDto;
import rice_monkey.payment.feign.toss.TossPaymentClient;
import rice_monkey.payment.feign.toss.dto.request.TossConfirmRequestDto;
import rice_monkey.payment.feign.toss.dto.response.TossConfirmResponseDto;

@Service
@RequiredArgsConstructor
public class TossPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentsClient;

    @Override
    @Transactional
    public void confirm(ConfirmRequestDto confirmRequestDto) {
        TossConfirmRequestDto requestDto = TossConfirmRequestDto.from(confirmRequestDto);

        TossConfirmResponseDto responseDto = tossPaymentsClient.confirm(requestDto);

        Payment payment = Payment.from(responseDto, 1L);

        paymentRepository.save(payment);
    }

}
