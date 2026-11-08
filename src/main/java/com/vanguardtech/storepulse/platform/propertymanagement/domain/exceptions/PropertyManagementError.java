package com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions;

/**
 * Business outcomes that prevent a Property Management operation from completing.
 *
 * <p>The lowercase name of each value is the error code answered by the REST API and the i18n key
 * the web application translates ({@code property.errors.<code>}).
 */
public enum PropertyManagementError {
    INVALID_GALLERY_NAME,
    INVALID_ADDRESS,
    INVALID_TOTAL_UNITS,
    GALLERY_NOT_FOUND,
    /** The administrator already registered a gallery; StorePulse manages one gallery per account. */
    GALLERY_ALREADY_REGISTERED,
    INVALID_UNIT_CODE,
    INVALID_FLOOR,
    INVALID_AREA,
    INVALID_BUSINESS_NAME,
    DUPLICATED_UNIT_CODE,
    DUPLICATED_COMMON_AREA_NAME,
    UNIT_NOT_FOUND,
    /** The unit still has a tenant, so it cannot be removed or put under maintenance. */
    UNIT_OCCUPIED,
    /** Only an active and available store can receive a tenant. */
    UNIT_NOT_AVAILABLE,
    COMMON_AREA_WITHOUT_TENANT,
    INVALID_EMAIL,
    INVITATION_NOT_FOUND,
    /** Only pending invitations can be revoked or accepted. */
    INVITATION_NOT_PENDING,
    INVITATION_EXPIRED,
    /** The invitation was sent to another email than the one of the tenant who accepts it. */
    INVITATION_EMAIL_MISMATCH,
    /** The request does not come from an active gallery administrator. */
    ADMINISTRATOR_NOT_AUTHENTICATED
}