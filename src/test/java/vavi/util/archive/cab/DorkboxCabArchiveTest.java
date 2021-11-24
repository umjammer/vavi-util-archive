/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.cab;

import org.junit.jupiter.api.Test;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * DorkboxCabArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/14 umjammer initial version <br>
 */
class DorkboxCabArchiveTest {

    @Test
    void test() throws Exception {
        Archive archive = new DorkboxCabArchive(DorkboxCabArchiveTest.class.getResourceAsStream("/test.cab"));
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName());
        }
        assertTrue(true);
    }
}

/* */
