package rice_monkey.access_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
        // 1) exchange → userId → mutatedExchange 흐름을 Mono<ServerWebExchange>로 구성
        Mono<ServerWebExchange> exchangeMono = exchange.getPrincipal()
                // 인증된 Principal(JwtAuthenticationToken)으로 캐스팅
                .cast(JwtAuthenticationToken.class)
                // JwtAuthenticationToken 에서 JWT 객체 꺼내기
                .map(token -> token.getToken().getClaimAsString("userId"))
                // userId claim이 빈 문자열이 아니면 계속, 아니면 이 Mono는 empty
                .filter(StringUtils::hasText)
                // userId가 있을 경우에만 ServerWebExchange를 새로운 요청으로 재구성
                .map(userId -> {
                    // 기존 요청에 X-User-Id 헤더를 추가
                    ServerHttpRequest newReq = exchange.getRequest()
                            .mutate()
                            .header("X-User-Id", userId)
                            .build();
                    // 새로 만든 request로 exchange도 다시 빌드
                    return exchange.mutate().request(newReq).build();
                })
                // userId claim이 없거나 인증 정보가 없으면 원본 exchange 사용
                .defaultIfEmpty(exchange);

        // 2) 최종적으로 구성된 exchangeMono를 chain.filter에 전달하여 Mono<Void>로 반환
        return exchangeMono.flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        // 인증 필터 다음(−100 + 10 = −90) 에 실행되도록 설정
        return SecurityWebFiltersOrder.AUTHENTICATION.getOrder() + 10;
    }

}
