package it.gov.pagopa.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlUtils {
    public static String normalizeUrl(String url) throws URISyntaxException {
        return new URI(url).normalize().toString();
    }

    public static String addQueryParams(String url, String key, String... values) {
        return UriComponentsBuilder.fromUriString(url).queryParam(key, values).build().toUriString();
    }
}
