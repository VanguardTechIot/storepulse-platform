package com.vanguardtech.storepulse.platform.iam.infrastructure.authorization;

import com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices.TokenService;
import com.vanguardtech.storepulse.platform.iam.domain.model.aggregates.UserAccount;
import com.vanguardtech.storepulse.platform.iam.domain.model.queries.GetUserAccountByIdQuery;
import com.vanguardtech.storepulse.platform.iam.domain.services.UserQueryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Authenticates each request from its {@code Authorization: Bearer <token>} header.
 *
 * <p>The account is read again on every request, so a disabled account loses access immediately
 * even while its token has not expired. Requests without a valid token continue unauthenticated and
 * the security rules decide whether they may proceed.
 *
 * <p>It is not a Spring bean on purpose: {@link SecurityConfiguration} adds it to the security
 * filter chain only, so it does not also run as a plain servlet filter.
 */
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenService tokenService;
    private final UserQueryService userQueryService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var token = resolveToken(request);
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            tokenService.getUserIdFromToken(token)
                    .flatMap(userId -> userQueryService.handle(new GetUserAccountByIdQuery(userId)))
                    .filter(UserAccount::isActive)
                    .ifPresent(userAccount -> authenticate(userAccount, request));
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) return null;
        var token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private void authenticate(UserAccount userAccount, HttpServletRequest request) {
        var principal = UserPrincipal.from(userAccount);
        var authentication = new UsernamePasswordAuthenticationToken(principal, null,
                List.of(new SimpleGrantedAuthority(principal.authority())));
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
