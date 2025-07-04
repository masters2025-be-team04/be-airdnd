package rice_monkey.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rice_monkey.member.oauth.OauthProvider;
import rice_monkey.member.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByOauthProviderAndOauthId(OauthProvider provider, String oauthId);

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByNickname(String nickname);
}
