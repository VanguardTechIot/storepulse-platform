package com.vanguardtech.storepulse.platform.assets.interfaces.rest.resources;

public record ResourceResource(
        Long id,
        String name,
        String description,
        String address,
        String floor,
        String reference,
        String status) {
}
