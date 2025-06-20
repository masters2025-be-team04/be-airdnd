package rice_monkey.listing.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingCreateRequest {

    @NotBlank(message = "숙소 이름은 필수입니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    @Positive(message = "가격은 양수여야 합니다.")
    private Integer price;

    @NotNull(message = "시작 날짜는 필수입니다.")
    private LocalDateTime startDate;

    @NotNull(message = "종료 날짜는 필수입니다.")
    @Future(message = "종료 날짜는 현재보다 미래여야 합니다.")
    private LocalDateTime endDate;

    @NotBlank(message = "숙소 유형은 필수입니다.")
    private String type;

    @NotNull(message = "최대 인원은 필수입니다.")
    @Min(value = 1, message = "최소 1명 이상 수용해야 합니다.")
    private Integer maxGuests;

    @NotBlank(message = "상태는 필수입니다.")
    private String status;

    @NotNull(message = "이미지는 필수입니다.")
    private MultipartFile image;

    @NotNull(message = "호스트 ID는 필수입니다.")
    private Long hostId;

    @NotNull(message = "주소는 필수입니다.")
    private AddressRequest address;

    @NotEmpty(message = "최소 하나 이상의 태그를 선택해야 합니다.")
    private List<Long> tagIds;
}
