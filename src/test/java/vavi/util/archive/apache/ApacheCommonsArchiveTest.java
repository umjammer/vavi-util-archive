/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.apache;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertTrue;


class ApacheCommonsArchiveTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "/test.zip",
        "/test.arj",
    })
    void test(String name) throws Exception {
        Archive archive = new ApacheCommonsArchive(ApacheCommonsArchiveTest.class.getResourceAsStream(name));
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(true);
    }
}

/* */
