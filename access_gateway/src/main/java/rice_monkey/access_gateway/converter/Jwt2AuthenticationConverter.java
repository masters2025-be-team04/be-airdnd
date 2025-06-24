package rice_monkey.access_gateway.converter;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class Jwt2AuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        String uid = jwt.getClaimAsString("userId");
        return Mono.just(new JwtAuthenticationToken(jwt, List.of(), uid));
    }

}
