package com.vanguardtech.storepulse.platform.assets.domain.exceptions;

/**
 * Business outcomes that prevent an IoT device or resource operation from completing.
 *
 * <p>The lowercase name of each value is the error code answered by the REST API, the same key
 * the web application can translate, for example {@code serial_number_already_registered}.
 */
public enum AssetsError {
    /** No IoT device has the requested id. */
    IOT_DEVICE_NOT_FOUND,
    /** No stand or common area has the requested id. */
    RESOURCE_NOT_FOUND,
    /** Another device is already registered with the same identifier. */
    SERIAL_NUMBER_ALREADY_REGISTERED,
    /** The device is already inactive, so it cannot be deactivated again. */
    IOT_DEVICE_ALREADY_INACTIVE,
    /** The device is already active, so it cannot be reactivated. */
    IOT_DEVICE_ALREADY_ACTIVE
}
