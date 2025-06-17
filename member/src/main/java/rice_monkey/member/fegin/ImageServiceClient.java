package rice_monkey.member.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "image-service", url = "${image.service.url}")
public interface ImageServiceClient {

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ImageUploadResponse uploadImage(@RequestPart("file") MultipartFile file);

    record ImageUploadResponse(
            Long imageId,
            String url
    ) {}
}
