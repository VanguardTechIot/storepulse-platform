package com.vanguardtech.storepulse.platform.shared.interfaces.rest.resources;

/** Plain confirmation message returned by endpoints that have no resource to answer with. */

public record MessageResource (String message) {
}
