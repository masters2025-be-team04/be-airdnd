package rice_monkey.listing.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QListing is a Querydsl query type for Listing
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QListing extends EntityPathBase<Listing> {

    private static final long serialVersionUID = -1308201095L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QListing listing = new QListing("listing");

    public final QAddress address;

    public final ListPath<ClosedStayDate, QClosedStayDate> closedStayDates = this.<ClosedStayDate, QClosedStayDate>createList("closedStayDates", ClosedStayDate.class, QClosedStayDate.class, PathInits.DIRECT2);

    public final ListPath<ListingComment, QListingComment> comments = this.<ListingComment, QListingComment>createList("comments", ListingComment.class, QListingComment.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> hostId = createNumber("hostId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> imgId = createNumber("imgId", Long.class);

    public final StringPath imgUrl = createString("imgUrl");

    public final NumberPath<Integer> maxGuests = createNumber("maxGuests", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final EnumPath<ListingStatus> status = createEnum("status", ListingStatus.class);

    public final ListPath<Tag, QTag> tags = this.<Tag, QTag>createList("tags", Tag.class, QTag.class, PathInits.DIRECT2);

    public final EnumPath<StayType> type = createEnum("type", StayType.class);

    public QListing(String variable) {
        this(Listing.class, forVariable(variable), INITS);
    }

    public QListing(Path<? extends Listing> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QListing(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QListing(PathMetadata metadata, PathInits inits) {
        this(Listing.class, metadata, inits);
    }

    public QListing(Class<? extends Listing> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new QAddress(forProperty("address")) : null;
    }

}

