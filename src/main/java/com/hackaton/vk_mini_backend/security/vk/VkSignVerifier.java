package com.hackaton.vk_mini_backend.security.vk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Проверяет корректность подписи параметров запуска VK Mini Apps и допустимое расхождение по времени.
 */
@Component
public class VkSignVerifier {

    private final String appSecretKey;
    private final long maxSkewSeconds;

    public VkSignVerifier(
            @Value("${vk.app.secret-key}") final String appSecretKey,
            @Value("${vk.verify.maxSkewSeconds}") final long maxSkewSeconds) {
        this.appSecretKey = appSecretKey;
        this.maxSkewSeconds = maxSkewSeconds;
    }

    public static Map<String, String> parseQueryToMap(final String rawQuery) {
        Map<String, String> params = new LinkedHashMap<>();
        if (rawQuery != null && !rawQuery.isBlank()) {
            String[] pairs = rawQuery.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx >= 0) {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    params.put(key, value);
                }
            }
        }
        return params;
    }

    public boolean validateSignature(final Map<String, String> params) {
        boolean valid = false;
        String providedSign = params.get("sign");
        if (providedSign != null && !providedSign.isBlank()) {
            Map<String, String> vkParams = collectVkParams(params);
            if (!vkParams.isEmpty()) {
                String base = buildBaseString(vkParams);
                String expected = hmacSha256Base64Url(base, appSecretKey);
                valid = expected.equals(providedSign);
            }
        }
        return valid;
    }

    private static Map<String, String> collectVkParams(final Map<String, String> params) {
        Map<String, String> vkParams = new TreeMap<>();
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (e.getKey().startsWith("vk_")) {
                vkParams.put(e.getKey(), e.getValue());
            }
        }
        return vkParams;
    }

    private static String buildBaseString(final Map<String, String> sortedParams) {
        StringBuilder data = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> e : sortedParams.entrySet()) {
            if (!first) {
                data.append('&');
            }
            first = false;
            data.append(e.getKey()).append('=').append(e.getValue());
        }
        return data.toString();
    }

    public boolean validateTimestamp(final Map<String, String> params) {
        boolean valid;
        String tsStr = params.get("vk_ts");
        if (tsStr == null) {
            valid = false;
        } else {
            try {
                long ts = Long.parseLong(tsStr);
                long now = System.currentTimeMillis() / 1000L;
                valid = Math.abs(now - ts) <= maxSkewSeconds;
            } catch (NumberFormatException e) {
                valid = false;
            }
        }
        return valid;
    }

    public Long extractUserId(final Map<String, String> params) {
        String id = params.get("vk_user_id");
        boolean hasParsed = false;
        long parsed = 0L;
        if (id != null) {
            try {
                parsed = Long.parseLong(id);
                hasParsed = true;
            } catch (NumberFormatException e) {
                hasParsed = false;
            }
        }
        return hasParsed ? Long.valueOf(parsed) : null;
    }

    private static String hmacSha256Base64Url(final String data, final String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String base64 = Base64.getEncoder().encodeToString(raw);
            // base64url без заполнения (padding)
            return base64.replace('+', '-').replace('/', '_').replaceAll("=+$", "");
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при вычислении HMAC: ", e);
        }
    }
}
