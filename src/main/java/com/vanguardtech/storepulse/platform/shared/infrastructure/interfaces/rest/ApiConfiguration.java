package com.vanguardtech.storepulse.platform.shared.infrastructure.interfaces.rest;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Prefixes every controller of the platform with {@code /api/v1}, the base path the web
 * application already uses (see {@code platformProviderApiBaseUrl} in its environment files).
 *
 * <p>Controllers declare only their own resource path, for example {@code /users}.
 */
@Configuration
public class ApiConfiguration implements WebMvcConfigurer {
    public static final String API_PREFIX = "/api/v1";
    private static final String BAS_PACKAGE = "com.vanguardtech.storepulse";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer){
        configurer.addPathPrefix(API_PREFIX,
                HandlerTypePredicate.forBasePackage(BAS_PACKAGE));
    }
}
