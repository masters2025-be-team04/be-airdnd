package rice_monkey.member.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import rice_monkey.member.domain.MemberRole;

@Getter
public class SignupRequest {

    private String loginId;

    private String password;

    private String nickname;

    private MemberRole role;

    private MultipartFile image;
}
