package rice_monkey.access_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
public class UserIdHeaderFilter implements GlobalFilter, Ordered {

    /**
     * filter() 메서드는 GatewayFilterChain에 연결되는 진입점입니다.
     * 여기서 ServerWebExchange를 가공하거나 그대로 전달할 수 있습니다.
     *
     * @param exchange 현재 HTTP 요청/응답 컨텍스트
     * @param chain    다음 필터 또는 최종 라우팅으로 넘어가는 체인
     * @return Mono<Void> 요청 처리가 끝나면 complete 신호만 전달
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<ServerWebExchange> mutated = exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                // 토큰에서 userId, roles 추출
                .map(token -> {
                    String userId = token.getToken().getClaimAsString("userId");

                    // 권한 정보 추출 (GrantedAuthority 에서 권한 문자열만)
                    String roles = token.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(","));

                    // userId, roles 헤더를 모두 추가
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Roles", roles)
                            .build();

                    return exchange.mutate().request(request).build();
                })
                // 인증 정보가 없거나 claim이 비어있으면 원본 exchange 사용
                .defaultIfEmpty(exchange);

        return mutated.flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        // 인증 필터 다음(−100 + 10 = −90) 에 실행되도록 설정
        return SecurityWebFiltersOrder.AUTHENTICATION.getOrder() + 10;
    }

}
