/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.zip;

import java.io.File;

import org.junit.jupiter.api.Test;

import vavi.util.archive.Archive;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * AntZipArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/14 umjammer initial version <br>
 */
class AntZipArchiveTest {

    @Test
    void test() throws Exception {
        Archive archive = new AntZipArchive(new File("src/test/resources/test.zip"));
        int c = 0;
        for (Entry<?> entry : archive.entries()) {
            System.err.println(entry.getName());
            c++;
        }
        assertEquals(7, c);
    }
}

/* */
