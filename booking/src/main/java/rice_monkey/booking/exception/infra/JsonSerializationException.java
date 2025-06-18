package rice_monkey.booking.exception.infra;

import org.springframework.http.HttpStatus;

public class JsonSerializationException extends InfrastructureException {

    public JsonSerializationException(String message) {
        super("INFRA_JSON_SERIALIZATION_ERROR", message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
