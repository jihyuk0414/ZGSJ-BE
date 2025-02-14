package com.example.API_Gateway;

import com.example.API_Gateway.filter.TokenCheckFilter;
import com.example.API_Gateway.util.CryptoUtil;
import com.example.API_Gateway.util.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil,CryptoUtil cryptoUtil) {
        List<String> permitUrls = new ArrayList<>();
        permitUrls.add("/president/login");
        permitUrls.add("/president/regist");
        permitUrls.add("commute/QRCheck");
        permitUrls.add("/leave-work");
        permitUrls.add("/go-to-work");
        permitUrls.add("/president/id-find"); // id 찾기는 인증 없이도 가능하도록 해야하니까
        permitUrls.add("/president/check/email");
        permitUrls.add("/president/resetPassword");
        permitUrls.add("/argocd");

        List<String> needIdUrls = new ArrayList<>();
        needIdUrls.add("/president/logout");
        needIdUrls.add("/president/refresh");
        needIdUrls.add("/president/modify");
        needIdUrls.add("/president/change-password");
        needIdUrls.add("/president/account-check");
        needIdUrls.add("/user/store");
        needIdUrls.add("/president/mypage");
        needIdUrls.add("/president/secession");
        needIdUrls.add("/user/account-check");
        needIdUrls.add("/user/president/validate-password");
        needIdUrls.add("/core/account/check");
        needIdUrls.add("/president/termaccept");
        needIdUrls.add("/core/account/certificate/pin");
        needIdUrls.add("/user/manager/check");

        return new TokenCheckFilter(jwtUtil, permitUrls, needIdUrls, cryptoUtil);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .formLogin( form-> form.disable())
                .httpBasic(basic-> basic.disable())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 프리플라이트 요청 캐시 시간 (초)

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
