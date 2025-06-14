package rice_monkey.booking.common.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rice_monkey.booking.common.advice.dto.ProblemDetail;
import rice_monkey.booking.exception.BusinessException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(BusinessException ex, HttpServletRequest req) {
        String requestId = req.getHeader("X-Request-ID");
        log.error("ERROR [{}] RequestID={} User={} URI={}", ex.getErrorCode(), requestId, req.getUserPrincipal(), req.getRequestURI(), ex);

        ProblemDetail body = new ProblemDetail(
                "https://api.your-domain.com/errors/" + ex.getErrorCode(),
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

}
