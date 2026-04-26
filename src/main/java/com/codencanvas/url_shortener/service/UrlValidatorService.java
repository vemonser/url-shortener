package com.codencanvas.url_shortener.service;

import java.net.InetAddress;
import java.net.URI;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.util.InvalidUrlException;

@Service
public class UrlValidatorService {

    private static final UrlValidator URL_VALIDATOR = new UrlValidator(new String[] { "http", "https" });

    private static final List<String> BLOCKED_PREFIXES = List.of(
            "192.168.", "10.", "172.16.", "127.", "0.", "localhost");

    public void validate(String url) {

        // 1. Format check
        if (!URL_VALIDATOR.isValid(url)) {
            throw new InvalidUrlException("Invalid URL format: " + url);
        }

        if (url.length() > 2048) {
            throw new InvalidUrlException("URL too long.");
        }

        checkForSSRF(url);
    }

    private void checkForSSRF(String url) {
        try {
            String host = URI.create(url).getHost();

            for (String blocked : BLOCKED_PREFIXES) {
                if (host.startsWith(blocked)) {
                    throw new InvalidUrlException(
                            "Internal URLs are not allowed.");
                }
            }

            InetAddress address = InetAddress.getByName(host);
            String resolvedIp = address.getHostAddress();

            for (String blocked : BLOCKED_PREFIXES) {
                if (resolvedIp.startsWith(blocked)) {
                    throw new InvalidUrlException(
                            "Internal URLs are not allowed.");
                }
            }

        } catch (InvalidUrlException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidUrlException("Could not resolve URL host.");
        }
    }
}
