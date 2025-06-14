package rice_monkey.listing.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rice_monkey.listing.domain.Listing;
import rice_monkey.listing.dto.ListingCreateRequest;
import rice_monkey.listing.dto.ListingDetailResponse;
import rice_monkey.listing.dto.ListingSearchCondition;
import rice_monkey.listing.service.ListingService;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    public ResponseEntity<Long> createListing(@RequestBody ListingCreateRequest request) {
        Long listingId = listingService.registerListing(request);
        return ResponseEntity.ok(listingId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingDetailResponse> getListingDetail(@PathVariable Long id) {
        ListingDetailResponse response = listingService.getListingDetail(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public void queryListingList(@ModelAttribute ListingSearchCondition condition){
        listingService.
    }

    private Double getActiveRating(Listing listing){
        return
    }


}

