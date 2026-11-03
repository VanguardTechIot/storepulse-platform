package com.vanguardtech.storepulse.platform.profilespreferences.domain.model.queries;

/**
 * Looks up the profile of an account (US-06).
 *
 * @param userId identifier of the account that owns the profile
 */
public record GetProfileByUserIdQuery(Long userId) {
}
