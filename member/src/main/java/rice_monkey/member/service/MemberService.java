package rice_monkey.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rice_monkey.member.domain.Member;
import rice_monkey.member.domain.MemberRole;
import rice_monkey.member.dto.SignupRequest;
import rice_monkey.member.exception.DuplicateMemberException;
import rice_monkey.member.fegin.ImageServiceClient;
import rice_monkey.member.oauth.OauthProvider;
import rice_monkey.member.repository.MemberRepository;
import rice_monkey.member.util.PasswordEncoderUtil;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {


    private final MemberRepository memberRepository;
    private final ImageServiceClient imageServiceClient;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void signup(SignupRequest request){
        if(memberRepository.findByLoginId(request.getLoginId()).isPresent()){
            throw new DuplicateMemberException("이미 존재하는 로그인 ID입니다.");
        }

        Long imageId = 0L;
        String imageUrl = "";
        if(request.getImage() != null || !request.getImage().isEmpty()){
            ImageServiceClient.ImageUploadResponse imageUploadResponse = imageServiceClient.uploadImage(request.getImage());
            imageId=imageUploadResponse.imageId();
            imageUrl=imageUploadResponse.url();
        }

        Member member = Member.builder()
                .loginId(request.getLoginId())
                .nickname(request.getNickname())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .role(MemberRole.USER)
                .oauthProvider(OauthProvider.LOCAL)
                .oauthId("local_" + request.getLoginId())
                .imgId(imageId)
                .imgUrl(imageUrl)
                .isDeleted(false)
                .build();

        memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("없는 사용자입니다."));
    }

    public void LogOut(Member member){
        member.setDeleted(true);
    }
}
