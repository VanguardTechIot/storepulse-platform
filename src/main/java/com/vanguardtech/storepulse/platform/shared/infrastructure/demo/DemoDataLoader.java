package com.vanguardtech.storepulse.platform.shared.infrastructure.demo;

import com.vanguardtech.storepulse.platform.shared.interfaces.rest.ApiConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registers a ready-to-use demo gallery the first time the platform starts: an administrator, two
 * tenants, the gallery with its units, IoT devices, two months of telemetry, alerts, utility bills,
 * a subscription and tenant communication. It runs once the platform accepts traffic, after every
 * catalog (resources, plans, safety rules and tariffs) is seeded, and does nothing when the demo
 * administrator already exists.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "storepulse.demo.enabled", havingValue = "true")
public class DemoDataLoader {

    static final String ADMINISTRATOR_EMAIL = "admin@storepulse.example";
    static final String FIRST_TENANT_EMAIL = "lucia.ramos@storepulse.example";
    static final String SECOND_TENANT_EMAIL = "jorge.paredes@storepulse.example";
    static final String PENDING_TENANT_EMAIL = "optica.vision@storepulse.example";
    static final String PASSWORD = "Demo2026";

    @EventListener
    public void on(AvailabilityChangeEvent<ReadinessState> event) {
        if (event.getState() != ReadinessState.ACCEPTING_TRAFFIC
                || !(event.getSource() instanceof WebServerApplicationContext context)
                || context.getWebServer() == null) return;
        var api = new DemoApiClient("http://localhost:" + context.getWebServer().getPort() + ApiConfiguration.API_PREFIX);
        try {
            if (signIn(api, ADMINISTRATOR_EMAIL) != null) return;
            load(api);
            log.info("Demo data ready: sign in as {} or {} with the password {}", ADMINISTRATOR_EMAIL,
                    FIRST_TENANT_EMAIL, PASSWORD);
        } catch (RestClientException exception) {
            log.warn("Demo data could not be loaded: {}", exception.getMessage());
        }
    }

    private void load(DemoApiClient api) {
        var admin = api.as(signUp(api, "Carlos Rojas", ADMINISTRATOR_EMAIL, "GALLERY_ADMINISTRATOR"));
        var firstTenant = api.as(signUp(api, "Lucia Ramos", FIRST_TENANT_EMAIL, "TENANT"));
        var secondTenant = api.as(signUp(api, "Jorge Paredes", SECOND_TENANT_EMAIL, "TENANT"));

        var galleryId = id(admin.post("/galleries",
                Map.of("name", "Galería Unión", "address", "Jr. de la Unión 845, Cercado de Lima", "totalUnits", 10)));
        var resources = new HashMap<String, Long>();
        admin.getList("/resources").forEach(resource -> resources.put((String) resource.get("name"), id(resource)));

        var unitA03 = unit(admin, galleryId, "STORE", "A-03", "1", 12.5, "Tienda Sol", resources.get("Stand A-03"));
        var unitA05 = unit(admin, galleryId, "STORE", "A-05", "1", 15.0, "Café Central", resources.get("Stand A-05"));
        unit(admin, galleryId, "STORE", "A-07", "1", 10.0, null, resources.get("Stand A-07"));
        var unitB07 = unit(admin, galleryId, "STORE", "B-07", "1", 11.0, "Óptica Visión", resources.get("Stand B-07"));
        unit(admin, galleryId, "STORE", "B-09", "1", 9.5, null, resources.get("Stand B-09"));
        unit(admin, galleryId, "STORE", "B-12", "1", 14.0, null, resources.get("Stand B-12"));
        unit(admin, galleryId, "STORE", "C-04", "2", 13.0, null, resources.get("Stand C-04"));
        unit(admin, galleryId, "STORE", "C-07", "2", 12.0, null, resources.get("Stand C-07"));
        unit(admin, galleryId, "COMMON_AREA", "HALL-1", "1", 80.0, "Main hall", resources.get("Main hall"));
        unit(admin, galleryId, "COMMON_AREA", "HALL-2", "2", 60.0, "Second floor hall", resources.get("Second floor hall"));

        acceptInvitation(admin, firstTenant, FIRST_TENANT_EMAIL, unitA03);
        acceptInvitation(admin, secondTenant, SECOND_TENANT_EMAIL, unitA05);
        admin.post("/units/" + unitB07 + "/invite", Map.of("email", PENDING_TENANT_EMAIL));

        var planId = admin.getList("/subscription-plans").stream()
                .filter(plan -> "COMMERCIAL".equals(plan.get("code"))).findFirst().map(DemoDataLoader::id).orElseThrow();
        admin.post("/subscriptions", Map.of("galleryId", galleryId, "monitoredUnitCount", 10, "planId", planId,
                "billingCycle", "MONTHLY", "paymentMethod", "CREDIT_CARD", "cardNumber", "4111111111111111",
                "cardExpiry", "12/30"));

        var today = LocalDate.now(ZoneOffset.UTC);
        var firstDay = YearMonth.from(today).minusMonths(2).atDay(1);
        consumption(admin, device(admin, "SP-CON-A03", "CONSUMPTION_NODE", resources.get("Stand A-03")), firstDay, today, 1.0);
        consumption(admin, device(admin, "SP-CON-A05", "CONSUMPTION_NODE", resources.get("Stand A-05")), firstDay, today, 1.4);
        consumption(admin, device(admin, "SP-CON-B07", "CONSUMPTION_NODE", resources.get("Stand B-07")), firstDay, today, 0.8);
        var hallNode = device(admin, "SP-AMB-H1", "COMMON_AREA_NODE", resources.get("Main hall"));
        var smokeNode = device(admin, "SP-SMK-A05", "SMOKE_NODE", resources.get("Stand A-05"));
        var upperHallNode = device(admin, "SP-AMB-H2", "COMMON_AREA_NODE", resources.get("Second floor hall"));
        device(admin, "SP-INT-H1", "INTRUSION_NODE", resources.get("Main hall"));
        environment(admin, hallNode, today.minusDays(13), today);
        environment(admin, upperHallNode, today.minusDays(13), today);
        for (var day = today.minusDays(6); !day.isAfter(today); day = day.plusDays(1))
            telemetry(admin, smokeNode, at(day, 12), Map.of("type", "SMOKE", "value", 12.0 + day.getDayOfMonth() % 5));
        alerts(admin, hallNode, smokeNode, upperHallNode);

        var lastMonth = YearMonth.from(today).minusMonths(1).toString();
        var billA03 = issuedBill(admin, unitA03, lastMonth);
        issuedBill(admin, unitA05, lastMonth);
        admin.post("/utility-bills", Map.of("unitId", unitB07, "period", lastMonth));
        firstTenant.post("/billing-disputes", Map.of("utilityBillId", billA03, "reason", "CONSUMPTION_ABOVE_BASELINE",
                "description", "La luz salió más alta que el mes pasado y el local tuvo el mismo horario."));

        admin.send("/tenant-notifications/announcements", Map.of("type", "ANNOUNCEMENT", "wholeGallery", true,
                "title", "Mantenimiento del sistema eléctrico",
                "body", "El sábado de 7:00 a 9:00 se cortará la luz en el primer piso por mantenimiento."));
        var conversationId = id(firstTenant.post("/conversations", Map.of("subject", "Fuga de agua en el baño del local")));
        firstTenant.post("/conversations/" + conversationId + "/messages",
                Map.of("content", "Hay una fuga debajo del lavadero desde la mañana.", "attachments", List.of()));
        admin.post("/conversations/" + conversationId + "/messages",
                Map.of("content", "Gracias por avisar. El técnico pasará hoy a las 4:00 p. m.", "attachments", List.of()));
        admin.post("/conversations", Map.of("tenantId", userId(api, SECOND_TENANT_EMAIL), "subject", "Renovación del contrato"));
    }

    private static String signUp(DemoApiClient api, String fullName, String email, String role) {
        api.post("/authentication/sign-up", Map.of("fullName", fullName, "email", email, "password", PASSWORD, "role", role));
        return signIn(api, email);
    }

    private static String signIn(DemoApiClient api, String email) {
        try {
            return (String) api.post("/authentication/sign-in", Map.of("email", email, "password", PASSWORD)).get("accessToken");
        } catch (HttpClientErrorException exception) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static Long userId(DemoApiClient api, String email) {
        var session = api.post("/authentication/sign-in", Map.of("email", email, "password", PASSWORD));
        return id((Map<String, Object>) session.get("user"));
    }

    private static Long unit(DemoApiClient admin, Long galleryId, String type, String code, String floor, double area,
                             String businessName, Long resourceId) {
        var body = new HashMap<String, Object>(Map.of("type", type, "code", code, "floor", floor,
                "areaSquareMeters", area, "resourceId", resourceId));
        if (businessName != null) body.put("businessName", businessName);
        return id(admin.post("/galleries/" + galleryId + "/units", body));
    }

    private static void acceptInvitation(DemoApiClient admin, DemoApiClient tenant, String email, Long unitId) {
        var invitation = admin.post("/units/" + unitId + "/invite", Map.of("email", email));
        tenant.post("/tenant-invitations/accept", Map.of("token", invitation.get("token")));
    }

    private static Long device(DemoApiClient admin, String serialNumber, String type, Long resourceId) {
        return id(admin.post("/iot-devices", Map.of("serialNumber", serialNumber, "type", type, "resourceId", resourceId)));
    }

    /** Daily electricity and water readings; the factor sets how much the unit consumes. */
    private static void consumption(DemoApiClient admin, Long deviceId, LocalDate from, LocalDate to, double factor) {
        for (var day = from; !day.isAfter(to); day = day.plusDays(1)) {
            var variation = (day.getDayOfMonth() * 7 % 10) / 10.0;
            telemetry(admin, deviceId, at(day, 21),
                    Map.of("type", "ELECTRICITY", "value", round((2.0 + variation) * factor)),
                    Map.of("type", "WATER", "value", round((0.08 + variation / 10) * factor)));
        }
    }

    private static void environment(DemoApiClient admin, Long deviceId, LocalDate from, LocalDate to) {
        for (var day = from; !day.isAfter(to); day = day.plusDays(1)) {
            var variation = day.getDayOfMonth() % 4;
            telemetry(admin, deviceId, at(day, 15),
                    Map.of("type", "TEMPERATURE", "value", 22.0 + variation),
                    Map.of("type", "HUMIDITY", "value", 55.0 + variation * 2));
        }
    }

    /** One open, one acknowledged and one resolved alert. */
    private static void alerts(DemoApiClient admin, Long hallNode, Long smokeNode, Long upperHallNode) {
        var now = Instant.now();
        telemetry(admin, upperHallNode, now.minus(2, ChronoUnit.DAYS), Map.of("type", "HUMIDITY", "value", 78.0));
        telemetry(admin, hallNode, now.minus(5, ChronoUnit.HOURS), Map.of("type", "TEMPERATURE", "value", 47.5));
        telemetry(admin, smokeNode, now.minus(30, ChronoUnit.MINUTES), Map.of("type", "SMOKE", "value", 420.0));
        var alerts = admin.getList("/alerts");
        alertOf(alerts, "HUMIDITY").ifPresent(alertId -> {
            admin.patch("/alerts/" + alertId + "/acknowledge", Map.of());
            admin.patch("/alerts/" + alertId + "/resolve",
                    Map.of("resolutionNote", "Se secó la filtración del techo y se ventiló el pasillo."));
        });
        alertOf(alerts, "TEMPERATURE").ifPresent(alertId -> admin.patch("/alerts/" + alertId + "/acknowledge", Map.of()));
    }

    private static Optional<Long> alertOf(List<Map<String, Object>> alerts, String sensorType) {
        return alerts.stream().filter(alert -> sensorType.equals(alert.get("sensorType"))).findFirst().map(DemoDataLoader::id);
    }

    @SafeVarargs
    private static void telemetry(DemoApiClient admin, Long deviceId, Instant recordedAt, Map<String, Object>... measurements) {
        var readings = Arrays.stream(measurements).map(measurement -> {
            var reading = new HashMap<>(measurement);
            reading.put("recordedAt", recordedAt.toString());
            return reading;
        }).toList();
        admin.post("/telemetry", Map.of("deviceId", deviceId, "receivedAt", recordedAt.toString(), "measurements", readings));
    }

    private static Long issuedBill(DemoApiClient admin, Long unitId, String period) {
        var billId = id(admin.post("/utility-bills", Map.of("unitId", unitId, "period", period)));
        admin.patch("/utility-bills/" + billId + "/verify-data", Map.of("acceptIncompleteData", false));
        admin.patch("/utility-bills/" + billId + "/issue", Map.of());
        return billId;
    }

    /** The given hour of the day, or a minute ago when that hour has not come yet today. */
    private static Instant at(LocalDate day, int hour) {
        var instant = day.atTime(hour, 0).toInstant(ZoneOffset.UTC);
        var latest = Instant.now().minus(1, ChronoUnit.MINUTES);
        return instant.isAfter(latest) ? latest : instant;
    }

    private static double round(double value) {
        return Math.round(value * 100) / 100.0;
    }

    private static Long id(Map<String, Object> body) {
        return ((Number) body.get("id")).longValue();
    }
}