/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.apache;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import vavi.util.Debug;
import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ApacheCommonsArchiveTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "/test.zip",
        "/test.arj", // TODO seems not work all
    })
    void test(String name) throws Exception {
        Archive archive = new ApacheCommonsArchive(ApacheCommonsArchiveTest.class.getResourceAsStream(name));
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(archive.entries().length > 0);
    }

    @Test
    void test1() throws Exception {
        ApacheCommonsArchiveSpi spi = new ApacheCommonsArchiveSpi();
        String[] suffixes = spi.getFileSuffixes();
Debug.println(Arrays.toString(suffixes));
        assertTrue(Arrays.asList(suffixes).contains("xz"));
        assertTrue(Arrays.asList(suffixes).contains("jar"));
        assertTrue(Arrays.asList(suffixes).contains("zst"));
        assertTrue(Arrays.asList(suffixes).contains("lzma"));
        assertFalse(Arrays.asList(suffixes).contains("Z")); // TODO just check the logic, not semantics (means .Z may be included by some compression type)
    }
}

/* */
