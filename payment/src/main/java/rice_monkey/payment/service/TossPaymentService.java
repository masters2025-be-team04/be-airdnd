package rice_monkey.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rice_monkey.payment.dao.PaymentRepository;
import rice_monkey.payment.domain.Payment;
import rice_monkey.payment.dto.request.ConfirmRequestDto;
import rice_monkey.payment.dto.response.ConfirmResponseDto;
import rice_monkey.payment.feign.booking.BookingClient;
import rice_monkey.payment.feign.toss.TossPaymentClient;
import rice_monkey.payment.feign.toss.dto.request.TossConfirmRequestDto;
import rice_monkey.payment.feign.toss.dto.response.TossConfirmResponseDto;

@Service
@RequiredArgsConstructor
public class TossPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentsClient;
    private final BookingClient bookingClient;

    @Override
    @Transactional
    public ConfirmResponseDto confirm(ConfirmRequestDto dto) {
        TossConfirmResponseDto tossConfirmResponseDto = tossPaymentsClient.confirm(TossConfirmRequestDto.from(dto));

        Payment payment = Payment.from(tossConfirmResponseDto, dto.bookingId());
        paymentRepository.save(payment);

        bookingClient.confirm(dto.bookingId());

        return new ConfirmResponseDto(payment.getBookingId());
    }

}
