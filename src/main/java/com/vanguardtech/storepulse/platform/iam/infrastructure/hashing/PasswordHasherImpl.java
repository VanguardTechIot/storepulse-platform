package com.vanguardtech.storepulse.platform.iam.infrastructure.hashing;

import com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * {@link PasswordHasher} backed by BCrypt, which salts every hash and is deliberately slow to
 * resist brute force.
 */
@Service
public class PasswordHasherImpl implements PasswordHasher {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String rawValue) {
        return passwordEncoder.encode(rawValue);
    }

    @Override
    public boolean matches(String rawValue, String encodedValue) {
        return passwordEncoder.matches(rawValue, encodedValue);
    }
}
