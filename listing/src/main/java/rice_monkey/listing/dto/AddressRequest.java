package rice_monkey.listing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rice_monkey.listing.domain.Address;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {

    @NotBlank(message = "전체 주소는 필수입니다.")
    private String fullAddress;

    @NotNull(message = "위도는 필수입니다.")
    @DecimalMin(value = "-90.0", inclusive = true, message = "위도는 -90 이상이어야 합니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    @DecimalMin(value = "-180.0", inclusive = true, message = "경도는 -180 이상이어야 합니다.")
    private Double longitude;

    public Address toValueObject() {
        return Address.builder()
                .fullAddress(fullAddress)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
