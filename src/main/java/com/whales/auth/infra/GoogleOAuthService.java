package com.whales.auth.infra;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GoogleOAuthService {

    private final String clientId;
    private final String clientSecret;
    private final String tokenUri;
    private final String userInfoUri;
    private final RestClient http;

    public GoogleOAuthService(
            @Value("${oauth2.google.client-id}") String clientId,
            @Value("${oauth2.google.client-secret}") String clientSecret,
            @Value("${oauth2.google.token-uri}") String tokenUri,
            @Value("${oauth2.google.userinfo-uri}") String userInfoUri
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(10_000);
        this.http = RestClient.builder().requestFactory(requestFactory).build();
    }

    public GoogleUser exchange(String code, String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri", redirectUri);
        form.add("grant_type", "authorization_code");

        GoogleToken token = http.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(GoogleToken.class);

        if (token == null || token.getAccessToken() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed to obtain Google access token");
        }

        GoogleUser user = http.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken())
                .retrieve()
                .body(GoogleUser.class);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed to fetch Google userinfo");
        }
        return user;
    }

    @Data
    public static class GoogleToken {
        @JsonProperty("access_token")
        private String accessToken;
    }

    @Data
    public static class GoogleUser {
        private String sub;
        private String email;
        @JsonProperty("email_verified")
        private boolean emailVerified;
        private String name;
    }
}
