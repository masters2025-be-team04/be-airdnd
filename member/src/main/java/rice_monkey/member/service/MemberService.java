package rice_monkey.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rice_monkey.member.dto.SignupRequest;
import rice_monkey.member.exception.DuplicateMemberException;
import rice_monkey.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public String signup(SignupRequest request){
        if(memberRepository.findByLoginId(request.getLoginId()).isPresent()){
            throw new DuplicateMemberException("이미 존재하는 로그인 ID입니다.");
        }
    }
}
