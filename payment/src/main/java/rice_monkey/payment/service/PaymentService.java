package rice_monkey.payment.service;

import rice_monkey.payment.dto.request.ConfirmRequestDto;
import rice_monkey.payment.dto.response.ConfirmResponseDto;

public interface PaymentService {

    ConfirmResponseDto confirm(ConfirmRequestDto confirmRequestDto);

}
