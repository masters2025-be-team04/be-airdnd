package rice_monkey.payment.service;

import rice_monkey.payment.dto.request.ConfirmRequestDto;

public interface PaymentService {

    void confirm(ConfirmRequestDto confirmRequestDto);

}
