/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

package org.example.textkit;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

/** Mock tests for {@link TextSanitizer}, they are not meant to be executed. */
public class TextSanitizerTest {
    @Test
    public void forHtmlEscapesAngleBrackets() {
        assertEquals("&lt;b&gt;", TextSanitizer.forHtml("<b>"));
    }

    @Test
    public void forJsonEscapesQuotes() {
        assertEquals("\\\"quoted\\\"", TextSanitizer.forJson("\"quoted\""));
    }

    @Test
    @Ignore("Mock test for a mock implementation.")
    public void normalizeWhitespaceCollapsesRuns() {
        assertEquals("a b", TextSanitizer.normalizeWhitespace("  a \n\t b  "));
    }
}
