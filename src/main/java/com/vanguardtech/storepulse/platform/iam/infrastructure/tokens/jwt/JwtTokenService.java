package com.vanguardtech.storepulse.platform.iam.infrastructure.tokens.jwt;

import com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices.TokenService;
import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * {@link TokenService} that issues JSON Web Tokens signed with HMAC-SHA.
 *
 * <p>The subject is the account identifier; the email and the role travel as claims for the
 * clients. The key comes from {@code storepulse.iam.jwt.secret} (Base64, at least 256 bits) and the
 * lifetime from {@code storepulse.iam.jwt.expiration-minutes}.
 */
@Slf4j
@Service
public class JwtTokenService implements TokenService {

    private static final String ISSUER = "storepulse-rest-api";
    private static final String EMAIL_CLAIM = "email";
    private static final String ROLE_CLAIM = "role";

    private final SecretKey signingKey;
    private final Duration expiration;

    /**
     * @param secret            Base64 signing key
     * @param expirationMinutes lifetime of every token
     * @throws io.jsonwebtoken.security.WeakKeyException when the key is shorter than 256 bits
     */
    public JwtTokenService(@Value("${storepulse.iam.jwt.secret}") String secret,
                           @Value("${storepulse.iam.jwt.expiration-minutes}") long expirationMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    @Override
    public String generateToken(UserAccount userAccount) {
        var issuedAt = Instant.now();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(String.valueOf(userAccount.getId()))
                .claim(EMAIL_CLAIM, userAccount.getEmail().address())
                .claim(ROLE_CLAIM, userAccount.getRole().name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plus(expiration)))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public Optional<Long> getUserIdFromToken(String token) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(Long.valueOf(claims.getSubject()));
        } catch (JwtException | IllegalArgumentException exception) {
            log.debug("Rejected access token: {}", exception.getMessage());
            return Optional.empty();
        }
    }
}
