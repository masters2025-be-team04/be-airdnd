package rice_monkey.booking.common.advice.dto;

public record ProblemDetail(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        String errorCode
) {
}
