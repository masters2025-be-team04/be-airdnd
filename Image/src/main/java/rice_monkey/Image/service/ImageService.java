package rice_monkey.Image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rice_monkey.Image.dto.ImageUploadResponse;
import rice_monkey.Image.entity.Image;
import rice_monkey.Image.repository.ImageRepository;
import rice_monkey.Image.util.S3Uploader;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Uploader s3Uploader;
    private final ImageRepository imageRepository;

    public ImageUploadResponse uploadImage(MultipartFile file) throws IOException {
        String url = s3Uploader.upload(file);

        Image image = imageRepository.save(Image.builder()
                .url(url)
                .status("ACTIVE")
                .build());

        return new ImageUploadResponse(image.getId(), image.getUrl());
    }
}

