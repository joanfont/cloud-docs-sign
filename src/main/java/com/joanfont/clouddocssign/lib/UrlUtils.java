package com.joanfont.clouddocssign.lib;

import org.springframework.web.util.UriComponentsBuilder;

public class UrlUtils {

    public static String urlWithPathFromUrl(String url, String newPath) {
        return UriComponentsBuilder
                .fromHttpUrl(url)
                .replacePath(newPath)
                .build()
                .toString();
    }
}
