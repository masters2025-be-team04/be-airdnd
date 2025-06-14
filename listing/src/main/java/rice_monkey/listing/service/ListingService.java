package rice_monkey.listing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rice_monkey.listing.Repository.ListingRepository;
import rice_monkey.listing.Repository.StayCommentRepository;
import rice_monkey.listing.Repository.TagRepository;
import rice_monkey.listing.domain.*;
import rice_monkey.listing.dto.ListingCreateRequest;
import rice_monkey.listing.dto.ListingDetailResponse;
import rice_monkey.listing.dto.ListingListQueryResponse;
import rice_monkey.listing.dto.ListingSearchCondition;
import rice_monkey.listing.exception.ListingNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;
    private final StayCommentRepository stayCommentRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Long registerListing(ListingCreateRequest request) {
        List<Tag> tags = tagRepository.findAllById(request.getTagIds());

        Listing listing = Listing.builder()
                .name(request.getName())
                .price(request.getPrice())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(ListingStatus.valueOf(request.getStatus()))
                .imgUrl(request.getImgUrl())
                .hostId(request.getHostId())
                .type(StayType.valueOf(request.getType()))
                .maxGuests(request.getMaxGuests())
                .address(request.getAddress().toValueObject())
                .tags(tags)
                .build();

        return listingRepository.save(listing).getId();
    }

    @Transactional
    public Listing findActiveListing(Long listingId) {
        return listingRepository.findById(listingId)
                .filter(listing -> listing.getStatus() == ListingStatus.AVAILABLE)
                .orElseThrow(() -> new IllegalArgumentException("숙소가 사용 불가능한 상태입니다."));
    }

    @Transactional(readOnly = true)
    public ListingDetailResponse getListingDetail(Long id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ListingNotFoundException("숙소를 찾을 수 없습니다."));

        return ListingDetailResponse.from(listing);
    }

    @Transactional(readOnly = true)
    public List<ListingListQueryResponse> getListingListWithQuery(ListingSearchCondition condition){
        List<Listing> listings = listingRepository.findAllByCondition(condition);
    }

    private Double getAverageActiveRating(Listing listing) {
        return stayCommentRepository.findCommentRatingAvg(listing.getId(), CommentStatus.ACTIVE);
    }


}

