package rice_monkey.messaging.handler;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    public Long getUserIdFromToken(String token) {
        // JWT 파싱 로직
        return Long.parseLong(Jwts.parser()
                .setSigningKey("your-secret")
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class));
    }
}

