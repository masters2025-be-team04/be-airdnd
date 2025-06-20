package rice_monkey.member.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import rice_monkey.member.domain.MemberRole;

@Getter
public class SignupRequest {

    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
    @NotEmpty
    private String nickname;
    @NotEmpty
    private MemberRole role;

    private MultipartFile image;
}
