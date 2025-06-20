package rice_monkey.listing.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClosedStayDate is a Querydsl query type for ClosedStayDate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClosedStayDate extends EntityPathBase<ClosedStayDate> {

    private static final long serialVersionUID = -379097954L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QClosedStayDate closedStayDate = new QClosedStayDate("closedStayDate");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QListing listing;

    public QClosedStayDate(String variable) {
        this(ClosedStayDate.class, forVariable(variable), INITS);
    }

    public QClosedStayDate(Path<? extends ClosedStayDate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QClosedStayDate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QClosedStayDate(PathMetadata metadata, PathInits inits) {
        this(ClosedStayDate.class, metadata, inits);
    }

    public QClosedStayDate(Class<? extends ClosedStayDate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.listing = inits.isInitialized("listing") ? new QListing(forProperty("listing"), inits.get("listing")) : null;
    }

}

