package rice_monkey.payment.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class TossFeignConfig {

    @Value("${toss.secret-key}")
    private String secretKey;

    @Bean
    public RequestInterceptor tossAuthInterceptor() {
        String encoded = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());
        return template -> template.header("Authorization", "Basic " + encoded);
    }

}
