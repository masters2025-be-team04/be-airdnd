package rice_monkey.listing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingCreateRequest {

    private String name;
    private Integer price;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String type;
    private Integer maxGuests;

    private String status;

    private String imgUrl;
    private Long hostId;

    private AddressRequest address;

    private List<Long> tagIds;  // 태그 ID 목록
}

