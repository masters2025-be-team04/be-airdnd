package rice_monkey.listing.fegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "image-service", url = "${image.service.url}")
public interface ImageServiceClient {

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ImageUploadResponse uploadImage(@RequestPart("file") MultipartFile file);

    record ImageUploadResponse(
            Long imageId,
            String url
    ) {}


    @GetMapping(value = "/images/{id}")
    ImageResponse getImageById(@PathVariable("id") Long id);

    record ImageResponse(
            Long id,
            String url,
            String fileName
    ) {}

    @DeleteMapping(value = "/images/{id}")
    void deleteImageById(@PathVariable("id") Long id);
}
