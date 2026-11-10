package com.vanguardtech.storepulse.platform.iam.application.internal.outboundservices;

/** One-way hashing of passwords and recovery codes. */
public interface PasswordHasher {

    /**
     * Hashes a secret with a random salt.
     *
     * @param rawValue plain password or recovery code
     * @return the encoded hash, safe to store
     */
    String encode(String rawValue);

    /**
     * Tells whether a plain secret corresponds to a stored hash.
     *
     * @param rawValue     plain password or recovery code
     * @param encodedValue hash produced by {@link #encode(String)}
     * @return {@code true} when they match
     */
    boolean matches(String rawValue, String encodedValue);
}
