/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

package org.example.textkit;

import java.util.List;

import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Turns arbitrary human readable titles into URL safe slugs.
 *
 * <p>This class is a mock and not a working implementation. It only exists so that the ORT analyzer
 * has some source code next to the {@code pom.xml} to look at.</p>
 */
public final class SlugGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlugGenerator.class);

    private static final int DEFAULT_MAX_LENGTH = 80;

    private final String separator;
    private final int maxLength;

    public SlugGenerator() {
        this("-", DEFAULT_MAX_LENGTH);
    }

    public SlugGenerator(String separator, int maxLength) {
        this.separator = separator;
        this.maxLength = maxLength;
    }

    /**
     * Return a slug for the given {@code title}, e.g. {@code "Hello World!"} becomes
     * {@code "hello-world"}.
     */
    public String slugify(String title) {
        LOGGER.debug("Creating a slug for '{}'.", title);
        throw new UnsupportedOperationException("Mock implementation.");
    }

    /**
     * Return a slug that is guaranteed not to collide with any of the {@code taken} slugs, by
     * appending a numeric suffix if needed.
     */
    public String slugifyUnique(String title, List<String> taken) {
        throw new UnsupportedOperationException("Mock implementation.");
    }

    /**
     * Return the given {@code slug} converted back into a capitalized title.
     */
    public String toTitle(String slug) {
        return WordUtils.capitalizeFully(slug.replace(separator, " "));
    }

    public int getMaxLength() {
        return maxLength;
    }
}
