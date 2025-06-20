package rice_monkey.listing.Repository;


import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import rice_monkey.listing.domain.QListing.Listing;
import rice_monkey.listing.dto.ListingSearchCondition;
import com.querydsl.core.BooleanBuilder;

import java.time.LocalDate;
import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.booleanTemplate;

@Repository
@RequiredArgsConstructor
public class ListingQueryDslRepositoryImpl implements ListingQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Listing> getListingsFilteredByCondition(ListingSearchCondition condition) {

        QListing listing = QListing.listing;
        BooleanBuilder builder = new BooleanBuilder();

        addDateCondition(condition, builder, listing);
        addGuestCountCondition(condition, builder, listing);
        addPriceCondition(condition, builder, listing);
        addLocationCondition(condition, builder, listing);

        return queryFactory
                .selectFrom(listing)
                .where(builder)
                .fetch();
    }

    private void addDateCondition(ListingSearchCondition condition, BooleanBuilder builder, QListing listing) {
        if (condition.hasDateCondition()) {
            List<LocalDate> reservationDates = condition.getReservationDates();
            builder.andNot(listing.closedStayDates.any().in(reservationDates));
        }
    }

    private void addGuestCountCondition(ListingSearchCondition condition, BooleanBuilder builder, QListing listing) {
        if (condition.hasGuestCountCondition()) {
            builder.and(listing.maxGuests.goe(condition.getGuestCount()));
        }
    }

    private void addPriceCondition(ListingSearchCondition condition, BooleanBuilder builder, QListing listing) {
        if (condition.hasPriceCondition()) {
            builder.and(listing.price.goe(condition.getMinPrice()))
                    .and(listing.price.loe(condition.getMaxPrice()));
        }
    }

    private void addLocationCondition(ListingSearchCondition condition, BooleanBuilder builder, QListing listing) {
        if (condition.hasLocationCondition()) {
            Double lat = condition.getLatitude();
            Double lng = condition.getLongitude();
            Integer distance = condition.getDistance();

            BooleanTemplate distanceCond = booleanTemplate(
                    "ST_Distance_Sphere(point({0}, {1}), point({2}, {3})) < {4}",
                    lng, lat, listing.location.longitude, listing.location.latitude, distance
            );
            builder.and(distanceCond);
        }
    }
}

