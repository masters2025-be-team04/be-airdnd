package rice_monkey.booking.exception.infra;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class InfrastructureException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    protected InfrastructureException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = status;
    }

}
