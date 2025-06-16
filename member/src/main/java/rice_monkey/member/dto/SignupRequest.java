package rice_monkey.member.dto;

import lombok.Getter;
import rice_monkey.member.domain.MemberRole;

@Getter
public class SignupRequest {

    String loginId;

    String password;

    String nickname;

    MemberRole role;
}
