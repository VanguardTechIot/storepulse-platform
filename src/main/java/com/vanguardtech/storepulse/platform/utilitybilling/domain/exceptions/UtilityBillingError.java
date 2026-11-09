package com.vanguardtech.storepulse.platform.utilitybilling.domain.exceptions;

/**
 * Business outcomes that prevent a utility billing operation from completing. The lowercase name
 * of each code is the key the web application translates (billing.errors.*).
 */
public enum UtilityBillingError {
    NOT_FOUND,
    UNIT_NOT_FOUND,
    /** The unit has no monitored resource linked, so its consumption cannot be read. */
    UNIT_NOT_MONITORED,
    DUPLICATE,
    INVALID_TRANSITION,
    INCOMPLETE_DATA_NOT_ACCEPTED,
    DISPUTE_CLOSED,
    RESOLUTION_REQUIRED,
    INVALID_AMOUNT,
    INVALID_PERIOD,
    INVALID_BILL,
    TARIFF_NOT_FOUND,
    NO_CONSUMPTION_DATA,
    TENANT_REQUIRED
}