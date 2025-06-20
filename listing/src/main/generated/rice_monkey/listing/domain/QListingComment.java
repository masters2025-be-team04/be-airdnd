package rice_monkey.listing.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QListingComment is a Querydsl query type for ListingComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QListingComment extends EntityPathBase<ListingComment> {

    private static final long serialVersionUID = 1929079974L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QListingComment listingComment = new QListingComment("listingComment");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QListing listing;

    public final NumberPath<Double> rating = createNumber("rating", Double.class);

    public final EnumPath<CommentStatus> status = createEnum("status", CommentStatus.class);

    public final StringPath writer = createString("writer");

    public QListingComment(String variable) {
        this(ListingComment.class, forVariable(variable), INITS);
    }

    public QListingComment(Path<? extends ListingComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QListingComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QListingComment(PathMetadata metadata, PathInits inits) {
        this(ListingComment.class, metadata, inits);
    }

    public QListingComment(Class<? extends ListingComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.listing = inits.isInitialized("listing") ? new QListing(forProperty("listing"), inits.get("listing")) : null;
    }

}

