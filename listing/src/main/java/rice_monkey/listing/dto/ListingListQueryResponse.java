package rice_monkey.listing.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import rice_monkey.listing.domain.StayType;

import java.util.List;


@RequiredArgsConstructor
@Builder
public class ListingListQueryResponse {

    private final Long id;
    private final String name;
    private final Integer price;
    private final Integer maxGuests;
    private final String address;
    private final Double latitude;
    private final Double longitude;
    private final String imageUrl;
    private final StayType type;
    private final Double rating;
    private final Integer commentCount;
    private final List<String> tagNames;



}
