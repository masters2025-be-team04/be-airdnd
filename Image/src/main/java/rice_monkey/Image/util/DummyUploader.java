package rice_monkey.Image.util;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DummyUploader implements Uploader {
    @Override
    public String upload(MultipartFile file) {
        System.out.println("📦 Dummy upload: " + file.getOriginalFilename());
        return "https://dummy.local/" + file.getOriginalFilename();
    }
}

