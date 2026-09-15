/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

package org.example.textkit;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Escapes untrusted text before it is embedded into HTML or JSON documents.
 *
 * <p>This class is a mock and not a working implementation.</p>
 */
public final class TextSanitizer {
    private TextSanitizer() {
        // Utility class, no instances.
    }

    /** Escape the given {@code text} so that it can be safely embedded into an HTML document. */
    public static String forHtml(String text) {
        return StringEscapeUtils.escapeHtml4(text);
    }

    /** Escape the given {@code text} so that it can be safely embedded into a JSON document. */
    public static String forJson(String text) {
        return StringEscapeUtils.escapeJson(text);
    }

    /** Collapse all runs of whitespace in {@code text} into a single space and trim the result. */
    public static String normalizeWhitespace(String text) {
        throw new UnsupportedOperationException("Mock implementation.");
    }
}
