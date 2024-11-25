package com.example.laptopshop.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.example.laptopshop.domain.dto.ResLoginDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

@Service
public class SecurityUtil {

    @Value("${hoidanit.jwt.base64-secret}")
    private String jwtKey;

    @Value("${hoidanit.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpriation; // ở file env -> string -> khi được gọi tự động ép thành kiểu long khi gọi ra

    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpriation; // ở file env -> string -> khi được gọi tự động ép thành kiểu long khi gọi ra

    private final JwtEncoder jwtEncoder;
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String createAccessToken(String email, ResLoginDTO.UserLogin dto) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpriation, ChronoUnit.SECONDS);

        List<String> listAuthority = new ArrayList<>();
        listAuthority.add("ROLE_USER_CREATE");
        listAuthority.add("ROLE_USER_UPDATE");

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", dto)
                .claim("permission", listAuthority)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    public Jwt checkValidRefreshToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        try {
            return jwtDecoder.decode(token); // -> ghi đè function decode có trong Interface JwtDecoder
        } catch (Exception e) {
            System.out.println("Refresh Token error " + e.getMessage());
            throw e;
        }
    }

    private SecretKey getSecretKey() {
        byte[] keyByte = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyByte, 0, keyByte.length, JWT_ALGORITHM.getName());
    }

    public String createRefreshToken(String email, ResLoginDTO dto) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpriation, ChronoUnit.SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", dto.getUserLogin())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
//    public static boolean isAuthenticated() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication != null && getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
//    }

//    public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority));
//    }

//    public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
//        return !hasCurrentUserAnyOfAuthorities(authorities);
//    }

//    public static boolean hasCurrentUserThisAuthority(String authority) {
//        return hasCurrentUserAnyOfAuthorities(authority);
//    }
//
//    private static Stream<String> getAuthorities(Authentication authentication) {
//        return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
//    }
}
