/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.archive.tar;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import vavi.util.archive.Archive;
import vavi.util.archive.Archives;
import vavi.util.archive.Entry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


/**
 * TarTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-23 nsano initial version <br>
 */
class TarTest {

    @Test
    public void test() throws Exception {
        Archive archive = new TarArchive(Files.newInputStream(Paths.get("src/test/resources/test.tar")));
        int c = 0;
        for (Entry entry : archive.entries()) {
            System.err.println(entry.getName() + "\t" + LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getTime()), ZoneId.systemDefault()));
            c++;
        }
        assertEquals(6, c);
    }

    @Test
    public void test1() throws Exception {
        InputStream stream = Archives.getInputStream(Paths.get("src/test/resources/test.tar"));
        assertInstanceOf(org.apache.commons.compress.archivers.tar.TarArchiveInputStream.class, stream);
    }
}
