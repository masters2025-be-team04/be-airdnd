package rice_monkey.booking.common.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.redisson.client.RedisBusyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rice_monkey.booking.common.advice.dto.ProblemDetail;
import rice_monkey.booking.exception.business.BusinessException;
import rice_monkey.booking.exception.infra.InfrastructureException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(BusinessException ex, HttpServletRequest req) {
        String requestId = req.getHeader("X-User-Id");
        log.error("ERROR [{}] RequestID={} User={} URI={}", ex.getErrorCode(), requestId, req.getUserPrincipal(), req.getRequestURI(), ex);

        ProblemDetail body = new ProblemDetail(
                // 오류 Url 임시
                "https://api.booking.com/errors/" + ex.getErrorCode(),
                ex.getMessage(),
                ex.getHttpStatus().value(),
                ex.getMessage(),
                req.getRequestURI(),
                ex.getErrorCode()
        );
        return ResponseEntity
                .status(ex.getHttpStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ProblemDetail> handleInfrastructure(InfrastructureException ex, HttpServletRequest req) {
        String requestId = req.getHeader("X-Request-ID");
        log.error("INFRA ERROR [{}] RequestID={} User={} URI={}", ex.getErrorCode(), requestId, req.getUserPrincipal(), req.getRequestURI(), ex);

        ProblemDetail body = new ProblemDetail(
                "https://api.booking.com/errors/" + ex.getErrorCode(),
                "내부 시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",  // 사용자에게는 친절한 메시지
                ex.getHttpStatus().value(),
                ex.getMessage(),
                req.getRequestURI(),
                ex.getErrorCode()
        );
        return ResponseEntity
                .status(ex.getHttpStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }

    @ExceptionHandler(RedisBusyException.class)
    public ResponseEntity<ProblemDetail> handleRedisBusy(RedisBusyException ex,
                                                         HttpServletRequest req) {
        String requestId = req.getHeader("X-Request-ID");
        log.warn("LOCK BUSY [{}] RequestID={} User={} URI={}",
                "BOOKING_LOCK_BUSY", requestId,
                req.getUserPrincipal(), req.getRequestURI(), ex);

        ProblemDetail body = new ProblemDetail(
                "https://api.booking.com/errors/BOOKING_LOCK_BUSY",
                "현재 다른 예약 처리 중입니다. 잠시 후 다시 시도해 주세요.",
                HttpStatus.TOO_MANY_REQUESTS.value(),
                ex.getMessage(),
                req.getRequestURI(),
                "BOOKING_LOCK_BUSY"
        );

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                // Retry-After 헤더로 프론트가 자동 재시도하게 할 수도 있습니다
                .header("Retry-After", "2")
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex, HttpServletRequest req) {
        String requestId = req.getHeader("X-Request-ID");
        log.error("GENERAL ERROR RequestID={} User={} URI={}", requestId, req.getUserPrincipal(), req.getRequestURI(), ex);

        ProblemDetail body = new ProblemDetail(
                "https://api.booking.com/errors/GENERAL_ERROR",
                "예기치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                req.getRequestURI(),
                "GENERAL_ERROR"
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }

}
