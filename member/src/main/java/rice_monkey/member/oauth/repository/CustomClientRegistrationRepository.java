package rice_monkey.member.oauth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import rice_monkey.member.oauth.SocialClientRegistration;

@Configuration
@RequiredArgsConstructor
public class CustomClientRegistrationRepository {

    private final SocialClientRegistration socialClientRegistration;

    public ClientRegistrationRepository getClientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(socialClientRegistration
                .naverClientRegistration(), socialClientRegistration.googleClientRegistration());
    }
}
