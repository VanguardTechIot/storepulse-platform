package com.vanguardtech.storepulse.platform.shared.infrastructure.demo;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Calls the public REST API of the platform with the token of a demo account, the same way the web
 * and mobile applications do, so the demo data goes through every business rule.
 */
class DemoApiClient {

    private static final ParameterizedTypeReference<Map<String, Object>> OBJECT = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LIST = new ParameterizedTypeReference<>() {
    };

    private final RestClient restClient;
    private final String accessToken;

    DemoApiClient(String baseUrl) {
        this(RestClient.create(baseUrl), null);
    }

    private DemoApiClient(RestClient restClient, String accessToken) {
        this.restClient = restClient;
        this.accessToken = accessToken;
    }

    /** @return a client that sends the given token in every request */
    DemoApiClient as(String token) {
        return new DemoApiClient(restClient, token);
    }

    Map<String, Object> post(String path, Object body) {
        return restClient.post().uri(path).headers(this::authorize).body(body).retrieve().body(OBJECT);
    }

    /** Sends a request whose answer is not needed, such as one that returns a list. */
    void send(String path, Object body) {
        restClient.post().uri(path).headers(this::authorize).body(body).retrieve().toBodilessEntity();
    }

    Map<String, Object> patch(String path, Object body) {
        return restClient.patch().uri(path).headers(this::authorize).body(body).retrieve().body(OBJECT);
    }

    List<Map<String, Object>> getList(String path) {
        return restClient.get().uri(path).headers(this::authorize).retrieve().body(LIST);
    }

    private void authorize(HttpHeaders headers) {
        if (accessToken != null) headers.setBearerAuth(accessToken);
    }
}