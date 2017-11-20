package com.mex.bidder.api.http.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * HTTP related common utilities.
 */
public final class HttpUtil {
    public static final DateTimeFormatter HTTP_DATE_FORMAT = // RFC 1123
            DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z");

    private HttpUtil() {
    }

    /**
     * @return A {@link URI} from the String URI
     * @throws IllegalArgumentException if a {@link URISyntaxException} is thrown
     */
    public static URI buildUri(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI: " + uri, e);
        }
    }

    /**
     * @return {@link Instant} parsed from RFC 1123 compliant date
     */
    public static Instant parseHttpDate(String dateText) {
        return HTTP_DATE_FORMAT.parseDateTime(dateText).toInstant();
    }

    /**
     * @return Text for the {@link Instant} represented in RFC 1123 date format
     */
    public static String formatHttpDate(Instant instant) {
        return HTTP_DATE_FORMAT.print(instant);
    }

    /**
     * @return Path elements concatenated together adding separator slashes as required
     */
    public static String concatPaths(String... paths) {
        StringBuilder buf = new StringBuilder();
        for (String path : paths) {
            if (buf.length() == 0 || buf.charAt(buf.length() - 1) != '/') {
                buf.append('/');
            }
            if (path.length() > 1 && path.charAt(0) == '/') {
                buf.append(path, 1, path.length());
            } else if (path.length() > 0) {
                buf.append(path);
            }
        }
        int len = buf.length();
        if (len == 0 || len == 1 && buf.charAt(0) == '/') {
            return "/";
        } else {
            if (buf.charAt(len - 1) == '/') {
                buf.deleteCharAt(len - 1);
            }
        }
        return buf.toString();
    }

    public static Multimap<String, String> toMultimap(String... params) {
        checkArgument(params.length % 2 == 0);
        ImmutableMultimap.Builder<String, String> mmap = ImmutableMultimap.builder();
        for (int i = 0; i < params.length; i += 2) {
            mmap.put(params[i], params[i + 1]);
        }
        return mmap.build();
    }


}
