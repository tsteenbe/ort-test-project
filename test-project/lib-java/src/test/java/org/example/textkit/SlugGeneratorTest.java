/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

package org.example.textkit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/** Mock tests for {@link SlugGenerator}, they are not meant to be executed. */
public class SlugGeneratorTest {
    private SlugGenerator generator;

    @Before
    public void setUp() {
        generator = new SlugGenerator();
    }

    @Test
    @Ignore("Mock test for a mock implementation.")
    public void slugifyLowercasesAndReplacesSpaces() {
        assertEquals("hello-world", generator.slugify("Hello World!"));
    }

    @Test
    @Ignore("Mock test for a mock implementation.")
    public void slugifyUniqueAppendsACounter() {
        assertEquals("hello-world-2", generator.slugifyUnique("Hello World", Arrays.asList("hello-world")));
    }

    @Test
    public void toTitleCapitalizesEveryWord() {
        assertEquals("Hello World", generator.toTitle("hello-world"));
    }

    @Test
    public void defaultMaxLengthIsEighty() {
        assertEquals(80, generator.getMaxLength());
    }
}
