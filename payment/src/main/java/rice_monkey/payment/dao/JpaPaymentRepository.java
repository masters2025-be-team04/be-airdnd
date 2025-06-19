package rice_monkey.payment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import rice_monkey.payment.domain.Payment;

import java.util.Optional;

public interface JpaPaymentRepository extends JpaRepository<Payment, Long> {
}
