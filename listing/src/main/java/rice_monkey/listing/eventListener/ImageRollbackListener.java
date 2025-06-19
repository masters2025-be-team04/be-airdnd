package rice_monkey.listing.eventListener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import rice_monkey.listing.fegin.ImageServiceClient;

@Component
public class ImageRollbackListener {

    private final ImageServiceClient imageServiceClient;

    public ImageRollbackListener(ImageServiceClient imageServiceClient) {
        this.imageServiceClient = imageServiceClient;
    }

    // 트랜잭션 롤백 시에만 호출됨
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleImageRollback(ImageUploadedEvent event) {
        Long imageId = event.imageId();
        if (imageId != null && imageId > 0) {
            imageServiceClient.deleteImageById(imageId); // 이미지 삭제
        }
    }
}

