package rice_monkey.Image.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageUploadResponse {
    private Long imageId;
    private String url;
}

