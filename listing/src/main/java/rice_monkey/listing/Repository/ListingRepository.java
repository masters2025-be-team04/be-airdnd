package rice_monkey.listing.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import rice_monkey.listing.domain.Listing;
import rice_monkey.listing.dto.ListingSearchCondition;

import java.util.List;
import java.util.Optional;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    @EntityGraph(attributePaths = {"tags", "comments"})

    Optional<Listing> findById(Long id);

    List<Listing> findAllByCondition(ListingSearchCondition condition);
}



