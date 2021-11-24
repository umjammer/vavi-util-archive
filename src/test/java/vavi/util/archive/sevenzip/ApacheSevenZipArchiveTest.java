/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.sevenzip;

import java.io.File;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * ApacheSevenZipArchiveTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/12 umjammer initial version <br>
 */
class ApacheSevenZipArchiveTest {

    @Test
    void test() throws Exception {
        ApacheSevenZipArchive sevenZip = new ApacheSevenZipArchive(new File("src/test/resources/test.7z"));
        assertEquals(20, sevenZip.entries().length);
    }
}

/* */
